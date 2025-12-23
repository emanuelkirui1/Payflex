package com.payflow.payrollsystem.integration;

import com.payflow.payrollsystem.model.Company;
import com.payflow.payrollsystem.model.PayrollRun;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.model.PayrollRunApproval;
import com.payflow.payrollsystem.repository.CompanyRepository;
import com.payflow.payrollsystem.repository.PayrollRunRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.sql.init.mode=never"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PayrollApprovalIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private com.payflow.payrollsystem.repository.PayrollRunApprovalRepository payrollRunApprovalRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private String baseUrl() { return "http://localhost:" + port; }

    private String buildToken(String email, String role, Long companyId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("companyId", companyId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Test
    void approval_flow_submit_and_approve_should_work() {
        // create company
        Company c = new Company();
        c.setName("IntegrationCo");
        c = companyRepository.save(c);

        // create users
        User hr = new User();
        hr.setEmail("hr@int.test");
        hr.setPassword("password");
        hr.setRole("HR");
        hr.setCompany(c);
        hr.setEnabled(true);
        userRepository.save(hr);

        User fin = new User();
        fin.setEmail("finance@int.test");
        fin.setPassword("password");
        fin.setRole("FINANCE");
        fin.setCompany(c);
        fin.setEnabled(true);
        userRepository.save(fin);

        // create payroll run
        PayrollRun run = new PayrollRun();
        run.setCreatedAt(LocalDateTime.now());
        run.setCreatedBy("hr@int.test");
        run.setRunDate(LocalDateTime.now());
        run.setStatus("DRAFT");
        run.setCompany(c);
        run = payrollRunRepository.save(run);

        // submit as HR
        String hrToken = buildToken(hr.getEmail(), "HR", c.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hrToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> submitResp = restTemplate.exchange(
                baseUrl() + "/api/payroll/runs/" + run.getId() + "/submit",
                HttpMethod.POST, entity, Void.class);

        assertEquals(HttpStatus.OK, submitResp.getStatusCode());
        PayrollRun updatedAfterSubmit = payrollRunRepository.findById(run.getId()).orElseThrow();
        assertEquals("REVIEW", updatedAfterSubmit.getStatus());

        // approve as FINANCE
        String finToken = buildToken(fin.getEmail(), "FINANCE", c.getId());
        HttpHeaders headersFin = new HttpHeaders();
        headersFin.setBearerAuth(finToken);
        headersFin.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> approveEntity = new HttpEntity<>("{\"comment\":\"All good\"}", headersFin);

        ResponseEntity<Void> approveResp = restTemplate.exchange(
                baseUrl() + "/api/payroll/runs/" + run.getId() + "/approve",
                HttpMethod.POST, approveEntity, Void.class);

        assertEquals(HttpStatus.OK, approveResp.getStatusCode());
        PayrollRun updatedAfterApprove = payrollRunRepository.findById(run.getId()).orElseThrow();
        assertEquals("LOCKED", updatedAfterApprove.getStatus());
        assertEquals(fin.getEmail(), updatedAfterApprove.getApprovedBy());

        // list approvals via repository
        List<PayrollRunApproval> approvals = payrollRunApprovalRepository.findByPayrollRunId(run.getId());

        assertTrue(approvals.size() >= 1);
        assertEquals("APPROVED", approvals.get(0).getDecision());
    }
}
