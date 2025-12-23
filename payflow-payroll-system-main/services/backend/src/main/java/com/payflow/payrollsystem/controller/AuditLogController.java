package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.AuditLog;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or hasRole('SUPERADMIN')")
    public Page<AuditLog> listAuditLogs(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (entityType != null) predicates.add(cb.equal(root.get("entityType"), entityType));
            if (action != null) predicates.add(cb.like(cb.lower(root.get("action")), "%" + action.toLowerCase() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return auditLogRepository.findAll(spec, PageRequest.of(page, size));
    }
}