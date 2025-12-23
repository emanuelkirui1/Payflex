package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.BillingPlan;
import com.payflow.payrollsystem.model.Company;
import com.payflow.payrollsystem.repository.BillingPlanRepository;
import com.payflow.payrollsystem.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private final CompanyRepository companyRepository;
    private final BillingPlanRepository billingPlanRepository;

    public SuperAdminController(CompanyRepository companyRepository, BillingPlanRepository billingPlanRepository) {
        this.companyRepository = companyRepository;
        this.billingPlanRepository = billingPlanRepository;
    }

    @GetMapping("/companies")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @PutMapping("/companies/{id}/deactivate")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> deactivateCompany(@PathVariable Long id) {
        Company company = companyRepository.findById(id).orElseThrow();
        // Logic to deactivate
        return ResponseEntity.ok("Deactivated");
    }

    @GetMapping("/billing-plans")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<BillingPlan>> getBillingPlans() {
        return ResponseEntity.ok(billingPlanRepository.findAll());
    }

    @PostMapping("/billing-plans")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<BillingPlan> addBillingPlan(@RequestBody BillingPlan plan) {
        return ResponseEntity.ok(billingPlanRepository.save(plan));
    }
}