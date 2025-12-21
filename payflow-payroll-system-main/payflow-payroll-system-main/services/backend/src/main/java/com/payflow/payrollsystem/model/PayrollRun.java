package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll_runs")
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_date")
    private LocalDateTime runDate;

    @Column(name = "status") // DRAFT, REVIEW, APPROVED, LOCKED
    private String status;

    @Column(name = "total_gross", precision = 15, scale = 2)
    private BigDecimal totalGross;

    @Column(name = "total_deductions", precision = 15, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "total_net", precision = 15, scale = 2)
    private BigDecimal totalNet;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "payrollRun")
    private List<Payslip> payslips;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getRunDate() { return runDate; }
    public void setRunDate(LocalDateTime runDate) { this.runDate = runDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalGross() { return totalGross; }
    public void setTotalGross(BigDecimal totalGross) { this.totalGross = totalGross; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getTotalNet() { return totalNet; }
    public void setTotalNet(BigDecimal totalNet) { this.totalNet = totalNet; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public List<Payslip> getPayslips() { return payslips; }
    public void setPayslips(List<Payslip> payslips) { this.payslips = payslips; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}