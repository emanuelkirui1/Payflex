package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.LeaveRequest;
import com.payflow.payrollsystem.model.LeaveRequestApproval;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.LeaveRequestApprovalRepository;
import com.payflow.payrollsystem.repository.LeaveRequestRepository;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {

    @Mock LeaveRequestRepository leaveRequestRepository;
    @Mock LeaveRequestApprovalRepository leaveRequestApprovalRepository;
    @Mock UserRepository userRepository;
    @Mock AuditLogRepository auditLogRepository;

    @Captor ArgumentCaptor<LeaveRequestApproval> approvalCaptor;
    @Captor ArgumentCaptor<com.payflow.payrollsystem.model.AuditLog> auditCaptor;

    LeaveRequestService leaveRequestService;

    @BeforeEach
    void setup() {
        leaveRequestService = new LeaveRequestService(leaveRequestRepository, leaveRequestApprovalRepository, userRepository, auditLogRepository);
    }

    @Test
    void createRequest_sets_pending_status_and_saves() {
        LeaveRequest req = new LeaveRequest();
        req.setEmployeeId(1L);
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(2));

        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest res = leaveRequestService.createRequest(req);
        assertEquals("PENDING", res.getStatus());
        verify(leaveRequestRepository).save(req);
    }

    @Test
    void approve_creates_approval_and_sets_status() {
        LeaveRequest req = new LeaveRequest();
        req.setId(3L);
        req.setStatus("PENDING");

        User u = new User();
        u.setId(10L);
        u.setEmail("hr@test.com");
        u.setRole("HR");

        when(leaveRequestRepository.findById(3L)).thenReturn(Optional.of(req));
        when(userRepository.findByEmail("hr@test.com")).thenReturn(Optional.of(u));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("hr@test.com", null));

        when(leaveRequestApprovalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest res = leaveRequestService.approveRequest(3L, "Enjoy");
        assertEquals("APPROVED", res.getStatus());

        verify(leaveRequestApprovalRepository).save(approvalCaptor.capture());
        LeaveRequestApproval saved = approvalCaptor.getValue();
        assertEquals("APPROVED", saved.getDecision());
        assertEquals("Enjoy", saved.getComment());
        assertEquals("hr@test.com", saved.getApproverEmail());

        verify(auditLogRepository).save(auditCaptor.capture());
        assertTrue(auditCaptor.getValue().getAction().contains("approved"));
    }

    @Test
    void reject_creates_approval_and_sets_status() {
        LeaveRequest req = new LeaveRequest();
        req.setId(4L);
        req.setStatus("PENDING");

        User u = new User();
        u.setId(11L);
        u.setEmail("hr@test.com");
        u.setRole("HR");

        when(leaveRequestRepository.findById(4L)).thenReturn(Optional.of(req));
        when(userRepository.findByEmail("hr@test.com")).thenReturn(Optional.of(u));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("hr@test.com", null));

        when(leaveRequestApprovalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest res = leaveRequestService.rejectRequest(4L, "Problem");
        assertEquals("REJECTED", res.getStatus());

        verify(leaveRequestApprovalRepository).save(approvalCaptor.capture());
        LeaveRequestApproval saved = approvalCaptor.getValue();
        assertEquals("REJECTED", saved.getDecision());
        assertEquals("Problem", saved.getComment());
        assertEquals("hr@test.com", saved.getApproverEmail());

        verify(auditLogRepository).save(auditCaptor.capture());
        assertTrue(auditCaptor.getValue().getAction().contains("rejected"));
    }

    @Test
    void listApprovals_delegates() {
        when(leaveRequestApprovalRepository.findByLeaveRequestId(5L)).thenReturn(List.of(new LeaveRequestApproval()));
        var list = leaveRequestService.listApprovals(5L);
        assertEquals(1, list.size());
        verify(leaveRequestApprovalRepository).findByLeaveRequestId(5L);
    }
}
