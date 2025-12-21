package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.AuditLog;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.AuditLogRepository;
import com.payflow.payrollsystem.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public String authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && password.equals(userOpt.get().getPassword())) {
            User user = userOpt.get();
            // Log successful login
            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setAction("User login");
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
            return generateToken(user);
        }
        throw new RuntimeException("Invalid credentials");
    }

    public String registerCompany(String name, String email, String password, String billingPlan) {
        // Create company, user, etc.
        // For simplicity, assume done
        return "Registered";
    }

    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .claim("companyId", user.getCompany() != null ? user.getCompany().getId() : null)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}