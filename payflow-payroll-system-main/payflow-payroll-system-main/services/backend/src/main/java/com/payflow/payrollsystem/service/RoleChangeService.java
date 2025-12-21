package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.AuditLog;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RoleChangeService {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationService notificationService;

    @Autowired
    public RoleChangeService(UserRepository userRepository, AuditLogRepository auditLogRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationService = notificationService;
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
    }

    private Optional<User> getCurrentUser() {
        String email = getCurrentUserEmail();
        return email == null ? Optional.empty() : userRepository.findByEmail(email);
    }

    @Transactional
    public User changeRole(Long targetUserId, String newRole) {
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User actor = getCurrentUser().orElseThrow(() -> new IllegalStateException("No authenticated user"));

        // Authorization and business rules
        // Super Admin can promote to COMPANY_ADMIN
        // Company Admin can promote to HR only and only once per user (promotedToHrAt)

        String actorRole = actor.getRole();
        String oldRole = target.getRole();

        if (newRole.equals("COMPANY_ADMIN")) {
            if (!"SUPER_ADMIN".equals(actorRole)) {
                throw new SecurityException("Only Super Admin can promote to COMPANY_ADMIN");
            }
            // allow promotion, record timestamp
            target.setRole("COMPANY_ADMIN");
            target.setPromotedToCompanyAdminAt(LocalDateTime.now());
        } else if (newRole.equals("HR")) {
            // Company admin may promote to HR, but only if target is in same company
            if (!"COMPANY_ADMIN".equals(actorRole)) {
                throw new SecurityException("Only Company Admins can promote to HR");
            }
            if (actor.getCompany() == null || target.getCompany() == null || !actor.getCompany().getId().equals(target.getCompany().getId())) {
                throw new SecurityException("Can only manage users in your company");
            }
            if (target.getPromotedToHrAt() != null) {
                throw new SecurityException("User has already been promoted to HR once");
            }
            target.setRole("HR");
            target.setPromotedToHrAt(LocalDateTime.now());
        } else {
            // allow other role changes only by Super Admin
            if (!"SUPER_ADMIN".equals(actorRole)) {
                throw new SecurityException("Only Super Admin can change roles to " + newRole);
            }
            target.setRole(newRole);
        }

        userRepository.save(target);

        // Create audit log
        AuditLog log = new AuditLog();
        log.setAction("ROLE_CHANGE");
        log.setEntityType("User");
        log.setEntityId(target.getId());
        log.setMetadata(String.format("actor=%s;old=%s;new=%s", actor.getEmail(), oldRole, target.getRole()));
        log.setUser(actor);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);

        // Notifications
        String message = String.format("Your role has changed from %s to %s", oldRole, target.getRole());
        notificationService.createInAppNotification(target, message);
        notificationService.sendEmailNotification(target, "Role changed", message);

        return target;
    }
}
