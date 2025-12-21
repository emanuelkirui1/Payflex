package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.AuditLog;
import com.payflow.payrollsystem.model.Employee;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock SalaryRepository salaryRepository;
    @Mock TaxRepository taxRepository;
    @Mock PayslipRepository payslipRepository;
    @Mock PayrollRunRepository payrollRunRepository;
    @Mock AuditLogRepository auditLogRepository;
    @Mock UserRepository userRepository;
    @Mock TaxCalculationService taxCalculationService;
    @Mock DeductionService deductionService;
    @Mock PensionService pensionService;

    @Captor ArgumentCaptor<AuditLog> auditLogCaptor;

    PayrollService payrollService;

    @BeforeEach
    void setup() {
        payrollService = new PayrollService(employeeRepository, salaryRepository, taxRepository, payslipRepository,
                auditLogRepository, userRepository, taxCalculationService, deductionService, pensionService);
    }

    @Test
    void addEmployee_creates_audit_with_user() {
        User u = new User();
        u.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(u));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test@example.com", null));

        Employee emp = new Employee();
        emp.setFirstName("Jane");
        emp.setLastName("Doe");

        when(employeeRepository.save(emp)).thenReturn(emp);

        payrollService.addEmployee(emp);

        verify(auditLogRepository).save(auditLogCaptor.capture());
        AuditLog saved = auditLogCaptor.getValue();
        assertNotNull(saved);
        assertEquals("Employee created: Jane Doe", saved.getAction());
        assertNotNull(saved.getTimestamp());
        assertNotNull(saved.getUser());
        assertEquals("test@example.com", saved.getUser().getEmail());
    }
}