package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.dto.RoleChangeRequest;
import com.payflow.payrollsystem.model.User;
import com.payflow.payrollsystem.service.RoleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class RoleController {
    private final RoleChangeService roleChangeService;

    @Autowired
    public RoleController(RoleChangeService roleChangeService) {
        this.roleChangeService = roleChangeService;
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
}
