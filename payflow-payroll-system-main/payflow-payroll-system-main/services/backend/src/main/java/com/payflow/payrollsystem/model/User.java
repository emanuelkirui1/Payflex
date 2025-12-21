package com.payflow.payrollsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String email;
    private String password;
    private String role;
    private Boolean enabled;

    @Column(name = "display_name")
    private String displayName;

    private String phone;

    @Column(name = "job_title")
    private String jobTitle;

    private String department;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "promoted_to_hr_at")
    private LocalDateTime promotedToHrAt;

    @Column(name = "promoted_to_company_admin_at")
    private LocalDateTime promotedToCompanyAdminAt;

    @OneToMany(mappedBy = "user")
    private List<Employee> employees;

    @OneToMany(mappedBy = "user")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<AuditLog> auditLogs;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; } 

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPromotedToHrAt() { return promotedToHrAt; }
    public void setPromotedToHrAt(LocalDateTime promotedToHrAt) { this.promotedToHrAt = promotedToHrAt; }

    public LocalDateTime getPromotedToCompanyAdminAt() { return promotedToCompanyAdminAt; }
    public void setPromotedToCompanyAdminAt(LocalDateTime promotedToCompanyAdminAt) { this.promotedToCompanyAdminAt = promotedToCompanyAdminAt; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public List<AuditLog> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<AuditLog> auditLogs) { this.auditLogs = auditLogs; }
}