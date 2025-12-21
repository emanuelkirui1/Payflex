package com.payflow.payrollsystem.integration;

import com.payflow.payrollsystem.model.Company;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.model.LeaveRequest;
import com.payflow.payrollsystem.model.LeaveRequestApproval;
import com.payflow.payrollsystem.repository.CompanyRepository;
import com.payflow.payrollsystem.repository.LeaveRequestRepository;
import com.payflow.payrollsystem.repository.LeaveRequestApprovalRepository;
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

import java.time.LocalDate;
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
public class LeaveRequestIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveRequestApprovalRepository leaveRequestApprovalRepository;

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
    void leave_request_create_and_approve_flow() {
        Company c = new Company();
        c.setName("LeaveCo");
        c = companyRepository.save(c);

        User emp = new User();
        emp.setEmail("employee@int.test");
        emp.setPassword("password");
        emp.setRole("EMPLOYEE");
        emp.setCompany(c);
        emp.setEnabled(true);
        userRepository.save(emp);

        User hr = new User();
        hr.setEmail("hr@int.test");
        hr.setPassword("password");
        hr.setRole("HR");
        hr.setCompany(c);
        hr.setEnabled(true);
        userRepository.save(hr);

        // create request as employee
        String empToken = buildToken(emp.getEmail(), "EMPLOYEE", c.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(empToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"employeeId\":%d,\"startDate\":\"%s\",\"endDate\":\"%s\",\"reason\":\"vacation\"}", emp.getId(), LocalDate.now(), LocalDate.now().plusDays(3));
        HttpEntity<String> createEntity = new HttpEntity<>(body, headers);

        ResponseEntity<LeaveRequest> createResp = restTemplate.postForEntity(baseUrl() + "/api/leaves", createEntity, LeaveRequest.class);
        assertEquals(HttpStatus.OK, createResp.getStatusCode());
        LeaveRequest created = createResp.getBody();
        assertNotNull(created);
        assertEquals("PENDING", created.getStatus());

        // approve as HR
        String hrToken = buildToken(hr.getEmail(), "HR", c.getId());
        HttpHeaders headersHr = new HttpHeaders();
        headersHr.setBearerAuth(hrToken);
        headersHr.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> approveEntity = new HttpEntity<>("{\"comment\":\"Looks good\"}", headersHr);

        ResponseEntity<LeaveRequest> approveResp = restTemplate.exchange(baseUrl() + "/api/leaves/" + created.getId() + "/approve", HttpMethod.POST, approveEntity, LeaveRequest.class);
        assertEquals(HttpStatus.OK, approveResp.getStatusCode());

        LeaveRequest updated = leaveRequestRepository.findById(created.getId()).orElseThrow();
        assertEquals("APPROVED", updated.getStatus());

        List<LeaveRequestApproval> approvals = leaveRequestApprovalRepository.findByLeaveRequestId(created.getId());
        assertTrue(approvals.size() >= 1);
        assertEquals("APPROVED", approvals.get(0).getDecision());
    }
}
