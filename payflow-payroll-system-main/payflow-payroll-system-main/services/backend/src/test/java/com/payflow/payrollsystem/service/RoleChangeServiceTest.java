package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.Company;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleChangeServiceTest {
    private UserRepository userRepository;
    private AuditLogRepository auditLogRepository;
    private NotificationService notificationService;
    private RoleChangeService roleChangeService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        auditLogRepository = mock(AuditLogRepository.class);
        notificationService = mock(NotificationService.class);
        roleChangeService = new RoleChangeService(userRepository, auditLogRepository, notificationService);
    }

    private void authAs(String email) {
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(email, "n/a"));
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void companyAdminPromoteToHr_onceAllowed_thenBlocked() {
        // set up actor (company admin)
        Company c = new Company(); c.setId(1L);
        User actor = new User(); actor.setId(10L); actor.setEmail("admin@c.com"); actor.setRole("COMPANY_ADMIN"); actor.setCompany(c);
        User target = new User(); target.setId(20L); target.setEmail("u@c.com"); target.setRole("EMPLOYEE"); target.setCompany(c);

        when(userRepository.findByEmail("admin@c.com")).thenReturn(Optional.of(actor));
        when(userRepository.findById(20L)).thenReturn(Optional.of(target));

        authAs("admin@c.com");

        // First promotion succeeds
        User updated = roleChangeService.changeRole(20L, "HR");
        assertEquals("HR", updated.getRole());
        assertNotNull(updated.getPromotedToHrAt());

        // Simulate persisted state
        when(userRepository.findById(20L)).thenReturn(Optional.of(updated));

        // Second promotion attempt fails
        SecurityException se = assertThrows(SecurityException.class, () -> roleChangeService.changeRole(20L, "HR"));
        assertTrue(se.getMessage().contains("already been promoted"));
    }

    @Test
    void superAdminCanPromoteToCompanyAdmin() {
        User actor = new User(); actor.setId(1L); actor.setEmail("super@x.com"); actor.setRole("SUPER_ADMIN");
        User target = new User(); target.setId(2L); target.setEmail("u@x.com"); target.setRole("EMPLOYEE");

        when(userRepository.findByEmail("super@x.com")).thenReturn(Optional.of(actor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        authAs("super@x.com");

        User updated = roleChangeService.changeRole(2L, "COMPANY_ADMIN");
        assertEquals("COMPANY_ADMIN", updated.getRole());
        assertNotNull(updated.getPromotedToCompanyAdminAt());
    }
}
