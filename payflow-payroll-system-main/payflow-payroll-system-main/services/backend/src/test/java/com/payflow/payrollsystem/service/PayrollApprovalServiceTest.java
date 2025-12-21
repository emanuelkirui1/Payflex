package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.PayrollRun;
import com.payflow.payrollsystem.model.PayrollRunApproval;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.PayrollRunApprovalRepository;
import com.payflow.payrollsystem.repository.PayrollRunRepository;
import com.payflow.payrollsystem.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollApprovalServiceTest {

    @Mock PayrollRunRepository payrollRunRepository;
    @Mock PayrollRunApprovalRepository payrollRunApprovalRepository;
    @Mock UserRepository userRepository;
    @Mock AuditLogRepository auditLogRepository;

    @Captor ArgumentCaptor<PayrollRunApproval> approvalCaptor;
    @Captor ArgumentCaptor<com.payflow.payrollsystem.model.AuditLog> auditCaptor;

    PayrollApprovalService payrollApprovalService;

    @BeforeEach
    void setup() {
        payrollApprovalService = new PayrollApprovalService(payrollRunRepository, payrollRunApprovalRepository, userRepository, auditLogRepository);
    }

    @Test
    void submitForReview_moves_draft_to_review_and_logs_audit() {
        PayrollRun run = new PayrollRun();
        run.setId(1L);
        run.setStatus("DRAFT");

        when(payrollRunRepository.findById(1L)).thenReturn(Optional.of(run));
        when(payrollRunRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PayrollRun res = payrollApprovalService.submitForReview(1L);

        assertEquals("REVIEW", res.getStatus());
        verify(payrollRunRepository).save(run);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertTrue(auditCaptor.getValue().getAction().contains("submitted for review"));
    }

    @Test
    void submitForReview_throws_when_not_draft() {
        PayrollRun run = new PayrollRun();
        run.setId(2L);
        run.setStatus("REVIEW");
        when(payrollRunRepository.findById(2L)).thenReturn(Optional.of(run));

        assertThrows(IllegalStateException.class, () -> payrollApprovalService.submitForReview(2L));
    }

    @Test
    void approveRun_creates_approval_and_locks_run() {
        PayrollRun run = new PayrollRun();
        run.setId(3L);
        run.setStatus("REVIEW");

        User u = new User();
        u.setId(10L);
        u.setEmail("finance@test.com");
        u.setRole("FINANCE");

        when(payrollRunRepository.findById(3L)).thenReturn(Optional.of(run));
        when(userRepository.findByEmail("finance@test.com")).thenReturn(Optional.of(u));

        // set security context
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("finance@test.com", null));

        when(payrollRunRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(payrollRunApprovalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PayrollRun res = payrollApprovalService.approveRun(3L, "Looks good");

        assertEquals("LOCKED", res.getStatus());
        assertEquals("finance@test.com", res.getApprovedBy());

        verify(payrollRunApprovalRepository).save(approvalCaptor.capture());
        PayrollRunApproval saved = approvalCaptor.getValue();
        assertEquals("APPROVED", saved.getDecision());
        assertEquals("Looks good", saved.getComment());
        assertEquals("finance@test.com", saved.getApproverEmail());

        verify(auditLogRepository).save(auditCaptor.capture());
        assertTrue(auditCaptor.getValue().getAction().contains("approved"));
    }

    @Test
    void rejectRun_creates_approval_and_moves_back_to_draft() {
        PayrollRun run = new PayrollRun();
        run.setId(4L);
        run.setStatus("REVIEW");

        User u = new User();
        u.setId(11L);
        u.setEmail("finance@test.com");
        u.setRole("FINANCE");

        when(payrollRunRepository.findById(4L)).thenReturn(Optional.of(run));
        when(userRepository.findByEmail("finance@test.com")).thenReturn(Optional.of(u));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("finance@test.com", null));

        when(payrollRunRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(payrollRunApprovalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PayrollRun res = payrollApprovalService.rejectRun(4L, "Issue found");

        assertEquals("DRAFT", res.getStatus());

        verify(payrollRunApprovalRepository).save(approvalCaptor.capture());
        PayrollRunApproval saved = approvalCaptor.getValue();
        assertEquals("REJECTED", saved.getDecision());
        assertEquals("Issue found", saved.getComment());
        assertEquals("finance@test.com", saved.getApproverEmail());

        verify(auditLogRepository).save(auditCaptor.capture());
        assertTrue(auditCaptor.getValue().getAction().contains("rejected"));
    }

    @Test
    void listApprovals_delegates_to_repository() {
        when(payrollRunApprovalRepository.findByPayrollRunId(5L)).thenReturn(List.of(new PayrollRunApproval()));
        List<PayrollRunApproval> list = payrollApprovalService.listApprovals(5L);
        assertEquals(1, list.size());
        verify(payrollRunApprovalRepository).findByPayrollRunId(5L);
    }
}
