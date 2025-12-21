package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal allowances;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "nssf_deduction", precision = 12, scale = 2)
    private BigDecimal nssfDeduction;

    @Column(name = "nhif_deduction", precision = 12, scale = 2)
    private BigDecimal nhifDeduction;

    @Column(name = "pension_contribution", precision = 12, scale = 2)
    private BigDecimal pensionContribution;

    @Column(name = "taxable_income", precision = 12, scale = 2)
    private BigDecimal taxableIncome;

    @Column(name = "paye_tax", precision = 12, scale = 2)
    private BigDecimal payeTax;

    @Column(name = "total_deductions", precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "net_pay", precision = 12, scale = 2)
    private BigDecimal netPay;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "salary")
    private List<Tax> taxes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getNssfDeduction() { return nssfDeduction; }
    public void setNssfDeduction(BigDecimal nssfDeduction) { this.nssfDeduction = nssfDeduction; }

    public BigDecimal getNhifDeduction() { return nhifDeduction; }
    public void setNhifDeduction(BigDecimal nhifDeduction) { this.nhifDeduction = nhifDeduction; }

    public BigDecimal getPensionContribution() { return pensionContribution; }
    public void setPensionContribution(BigDecimal pensionContribution) { this.pensionContribution = pensionContribution; }

    public BigDecimal getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(BigDecimal taxableIncome) { this.taxableIncome = taxableIncome; }

    public BigDecimal getPayeTax() { return payeTax; }
    public void setPayeTax(BigDecimal payeTax) { this.payeTax = payeTax; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Tax> getTaxes() { return taxes; }
    public void setTaxes(List<Tax> taxes) { this.taxes = taxes; }
}