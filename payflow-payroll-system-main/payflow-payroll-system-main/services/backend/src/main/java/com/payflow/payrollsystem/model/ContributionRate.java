package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contribution_rates")
public class ContributionRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate", precision = 5, scale = 4)
    private BigDecimal rate; // e.g., 0.06 for 6%

    @Column(name = "max_pensionable_earnings", precision = 12, scale = 2)
    private BigDecimal maxPensionableEarnings;

    @Column(name = "type") // e.g., "NSSF_EMPLOYEE", "NSSF_EMPLOYER"
    private String type;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getMaxPensionableEarnings() { return maxPensionableEarnings; }
    public void setMaxPensionableEarnings(BigDecimal maxPensionableEarnings) { this.maxPensionableEarnings = maxPensionableEarnings; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
}