package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_run_approvals")
public class PayrollRunApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payroll_run_id")
    private PayrollRun payrollRun;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_email")
    private String approverEmail;

    @Column(name = "role")
    private String role;

    @Column(name = "decision")
    private String decision; // APPROVED / REJECTED

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PayrollRun getPayrollRun() { return payrollRun; }
    public void setPayrollRun(PayrollRun payrollRun) { this.payrollRun = payrollRun; }

    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }

    public String getApproverEmail() { return approverEmail; }
    public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
