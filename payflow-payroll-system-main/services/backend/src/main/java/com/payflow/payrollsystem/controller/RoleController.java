package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.dto.RoleChangeRequest;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.service.RoleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.payflow.payrollsystem.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class RoleController {
    private final RoleChangeService roleChangeService;
    private final UserRepository userRepository;

    @Autowired
    public RoleController(RoleChangeService roleChangeService, UserRepository userRepository) {
        this.roleChangeService = roleChangeService;
        this.userRepository = userRepository;
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody RoleChangeRequest req) {
        try {
            User updated = roleChangeService.changeRole(id, req.getRole());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal error");
        }
    }

    @GetMapping("")
    public ResponseEntity<?> listUsers() {
        try {
            // Determine current user
            String email = SecurityContextHolder.getContext().getAuthentication() != null
                    ? SecurityContextHolder.getContext().getAuthentication().getName()
                    : null;
            if (email == null) return ResponseEntity.status(401).body("Not authenticated");

            User actor = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

            if ("SUPER_ADMIN".equals(actor.getRole())) {
                return ResponseEntity.ok(userRepository.findAll());
            } else if ("COMPANY_ADMIN".equals(actor.getRole())) {
                if (actor.getCompany() == null) return ResponseEntity.ok(java.util.List.of());
                return ResponseEntity.ok(userRepository.findAll().stream().filter(u -> u.getCompany() != null && u.getCompany().getId().equals(actor.getCompany().getId())).toList());
            } else {
                return ResponseEntity.status(403).body("Forbidden");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal error");
        }
    }
}
