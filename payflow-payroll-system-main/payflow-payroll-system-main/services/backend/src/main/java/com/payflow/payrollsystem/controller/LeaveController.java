package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.LeaveRequest;
import com.payflow.payrollsystem.model.LeaveRequestApproval;
import com.payflow.payrollsystem.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    private final LeaveRequestService leaveRequestService;

    public LeaveController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> create(@RequestBody LeaveRequest req) {
        return ResponseEntity.ok(leaveRequestService.createRequest(req));
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> list() {
        return ResponseEntity.ok(leaveRequestService.listRequests(null));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequest> approve(@PathVariable Long id, @RequestBody(required = false) Map<String,String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(leaveRequestService.approveRequest(id, comment));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveRequest> reject(@PathVariable Long id, @RequestBody(required = false) Map<String,String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(leaveRequestService.rejectRequest(id, comment));
    }

    @GetMapping("/{id}/approvals")
    public ResponseEntity<List<LeaveRequestApproval>> approvals(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.listApprovals(id));
    }
}
