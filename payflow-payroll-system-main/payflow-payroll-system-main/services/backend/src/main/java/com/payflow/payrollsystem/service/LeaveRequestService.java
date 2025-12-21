package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.LeaveRequest;
import com.payflow.payrollsystem.model.LeaveRequestApproval;
import com.payflow.payrollsystem.repository.LeaveRequestApprovalRepository;
import com.payflow.payrollsystem.repository.LeaveRequestRepository;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.model.AuditLog;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestApprovalRepository leaveRequestApprovalRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               LeaveRequestApprovalRepository leaveRequestApprovalRepository,
                               UserRepository userRepository,
                               AuditLogRepository auditLogRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveRequestApprovalRepository = leaveRequestApprovalRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
    }

    private Optional<User> getCurrentUser() {
        String email = getCurrentUserEmail();
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email);
    }

    public LeaveRequest createRequest(LeaveRequest req) {
        req.setStatus("PENDING");
        return leaveRequestRepository.save(req);
    }

    @Transactional
    public LeaveRequest approveRequest(Long id, String comment) {
        LeaveRequest r = leaveRequestRepository.findById(id).orElseThrow();
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) throw new IllegalStateException("Only PENDING requests can be approved");
        LeaveRequestApproval approval = new LeaveRequestApproval();
        approval.setLeaveRequest(r);
        String email = getCurrentUserEmail();
        if (email != null) {
            approval.setApproverEmail(email);
            getCurrentUser().ifPresent(u -> { approval.setApproverId(u.getId()); approval.setRole(u.getRole()); });
        }
        approval.setDecision("APPROVED");
        approval.setComment(comment);
        approval.setCreatedAt(LocalDateTime.now());
        leaveRequestApprovalRepository.save(approval);
        r.setStatus("APPROVED");
        leaveRequestRepository.save(r);
        logAudit("Leave request approved", "LeaveRequest", r.getId(), comment);
        return r;
    }

    @Transactional
    public LeaveRequest rejectRequest(Long id, String comment) {
        LeaveRequest r = leaveRequestRepository.findById(id).orElseThrow();
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) throw new IllegalStateException("Only PENDING requests can be rejected");
        LeaveRequestApproval approval = new LeaveRequestApproval();
        approval.setLeaveRequest(r);
        String email = getCurrentUserEmail();
        if (email != null) {
            approval.setApproverEmail(email);
            getCurrentUser().ifPresent(u -> { approval.setApproverId(u.getId()); approval.setRole(u.getRole()); });
        }
        approval.setDecision("REJECTED");
        approval.setComment(comment);
        approval.setCreatedAt(LocalDateTime.now());
        leaveRequestApprovalRepository.save(approval);
        r.setStatus("REJECTED");
        leaveRequestRepository.save(r);
        logAudit("Leave request rejected", "LeaveRequest", r.getId(), comment);
        return r;
    }

    public List<LeaveRequestApproval> listApprovals(Long requestId) {
        return leaveRequestApprovalRepository.findByLeaveRequestId(requestId);
    }

    public List<LeaveRequest> listRequests(Long companyId) {
        // simple list for now; production would filter by company/permissions
        return leaveRequestRepository.findAll();
    }

    private void logAudit(String action, String entityType, Long entityId, String metadata) {
        String email = getCurrentUserEmail();
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        if (entityType != null) log.setEntityType(entityType);
        if (entityId != null) log.setEntityId(entityId);
        if (metadata != null) log.setMetadata(metadata);
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(log::setUser);
        }
        auditLogRepository.save(log);
    }
}
