package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "billing_plans")
public class BillingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String features; // or use JSON type

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "billingPlan")
    private List<CompanyBilling> companyBillings;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<CompanyBilling> getCompanyBillings() { return companyBillings; }
    public void setCompanyBillings(List<CompanyBilling> companyBillings) { this.companyBillings = companyBillings; }
}