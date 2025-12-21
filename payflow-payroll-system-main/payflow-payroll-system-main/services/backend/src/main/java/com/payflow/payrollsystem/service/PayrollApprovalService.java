package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.PayrollRun;
import com.payflow.payrollsystem.model.PayrollRunApproval;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.PayrollRunApprovalRepository;
import com.payflow.payrollsystem.repository.PayrollRunRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollApprovalService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayrollRunApprovalRepository payrollRunApprovalRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public PayrollApprovalService(PayrollRunRepository payrollRunRepository,
                                  PayrollRunApprovalRepository payrollRunApprovalRepository,
                                  UserRepository userRepository,
                                  AuditLogRepository auditLogRepository) {
        this.payrollRunRepository = payrollRunRepository;
        this.payrollRunApprovalRepository = payrollRunApprovalRepository;
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

    @Transactional
    public PayrollRun submitForReview(Long runId) {
        PayrollRun run = payrollRunRepository.findById(runId).orElseThrow();
        if (!"DRAFT".equalsIgnoreCase(run.getStatus())) {
            throw new IllegalStateException("Only DRAFT runs can be submitted");
        }
        run.setStatus("REVIEW");
        payrollRunRepository.save(run);
        logAudit("Payroll run submitted for review", "PayrollRun", run.getId(), null);
        return run;
    }

    @Transactional
    public PayrollRun approveRun(Long runId, String comment) {
        PayrollRun run = payrollRunRepository.findById(runId).orElseThrow();
        if (!"REVIEW".equalsIgnoreCase(run.getStatus())) {
            throw new IllegalStateException("Only REVIEW runs can be approved");
        }
        // debug: print security context and current user
        System.out.println("[DEBUG] Auth: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("[DEBUG] currentUserEmail: " + getCurrentUserEmail());
        getCurrentUser().ifPresentOrElse(u -> System.out.println("[DEBUG] Found user: " + u.getEmail()), () -> System.out.println("[DEBUG] User not found in repository"));

        PayrollRunApproval approval = new PayrollRunApproval();
        approval.setPayrollRun(run);
        String email = getCurrentUserEmail();
        if (email != null) {
            approval.setApproverEmail(email);
            userRepository.findByEmail(email).ifPresent(u -> {
                approval.setApproverId(u.getId());
                approval.setRole(u.getRole());
            });
        }
        approval.setDecision("APPROVED");
        approval.setComment(comment);
        approval.setCreatedAt(LocalDateTime.now());
        payrollRunApprovalRepository.save(approval);

        run.setApprovedBy(email);
        run.setStatus("LOCKED");
        payrollRunRepository.save(run);
        logAudit("Payroll run approved", "PayrollRun", run.getId(), comment);
        return run;
    }

    @Transactional
    public PayrollRun rejectRun(Long runId, String comment) {
        PayrollRun run = payrollRunRepository.findById(runId).orElseThrow();
        if (!"REVIEW".equalsIgnoreCase(run.getStatus())) {
            throw new IllegalStateException("Only REVIEW runs can be rejected");
        }
        PayrollRunApproval approval = new PayrollRunApproval();
        approval.setPayrollRun(run);
        String email = getCurrentUserEmail();
        if (email != null) {
            approval.setApproverEmail(email);
            userRepository.findByEmail(email).ifPresent(u -> {
                approval.setApproverId(u.getId());
                approval.setRole(u.getRole());
            });
        }
        approval.setDecision("REJECTED");
        approval.setComment(comment);
        approval.setCreatedAt(LocalDateTime.now());
        payrollRunApprovalRepository.save(approval);

        run.setStatus("DRAFT");
        payrollRunRepository.save(run);
        logAudit("Payroll run rejected", "PayrollRun", run.getId(), comment);
        return run;
    }

    public List<PayrollRunApproval> listApprovals(Long runId) {
        return payrollRunApprovalRepository.findByPayrollRunId(runId);
    }

    private void logAudit(String action, String entityType, Long entityId, String metadata) {
        String email = getCurrentUserEmail();
        com.payflow.payrollsystem.model.AuditLog log = new com.payflow.payrollsystem.model.AuditLog();
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
