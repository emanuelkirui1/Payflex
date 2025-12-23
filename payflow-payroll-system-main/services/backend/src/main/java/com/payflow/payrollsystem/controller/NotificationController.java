package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.Notification;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.UserRepository;
import com.payflow.payrollsystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
    }

    @GetMapping("")
    public ResponseEntity<?> list() {
        String email = getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(401).body("Not authenticated");
        Optional<User> u = userRepository.findByEmail(email);
        if (u.isEmpty()) return ResponseEntity.status(404).body("User not found");
        List<Notification> list = notificationService.listForUser(u.get().getId());
        return ResponseEntity.ok(list);
    }
}
