package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.Notification;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createInAppNotification(User user, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        return notificationRepository.save(n);
    }

    public void sendEmailNotification(User user, String subject, String body) {
        // Email sending is stubbed for now; if SMTP is configured later we can integrate JavaMailSender.
        logger.info("[EMAIL STUB] To: {} Subject: {} Body: {}", user.getEmail(), subject, body);
    }

    public List<Notification> listForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
