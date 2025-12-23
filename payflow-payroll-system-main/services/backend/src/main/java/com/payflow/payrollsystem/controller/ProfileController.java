package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.dto.ProfileDto;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserRepository userRepository;

    @Autowired
    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String email = getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(401).body("Not authenticated");
        Optional<User> u = userRepository.findByEmail(email);
        if (u.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = u.get();
        ProfileDto dto = toDto(user);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody ProfileDto dto) {
        String email = getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(401).body("Not authenticated");
        Optional<User> u = userRepository.findByEmail(email);
        if (u.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = u.get();
        // update allowed fields
        user.setDisplayName(dto.getDisplayName());
        user.setPhone(dto.getPhone());
        user.setJobTitle(dto.getJobTitle());
        user.setDepartment(dto.getDepartment());
        user.setBio(dto.getBio());
        user.setAvatarUrl(dto.getAvatarUrl());
        userRepository.save(user);
        return ResponseEntity.ok(toDto(user));
    }

    private ProfileDto toDto(User u) {
        ProfileDto dto = new ProfileDto();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setDisplayName(u.getDisplayName());
        dto.setPhone(u.getPhone());
        dto.setJobTitle(u.getJobTitle());
        dto.setDepartment(u.getDepartment());
        dto.setBio(u.getBio());
        dto.setAvatarUrl(u.getAvatarUrl());
        return dto;
    }
}
