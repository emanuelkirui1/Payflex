package com.payflow.payrollsystem.controller;

import com.payflow.payrollsystem.model.PayrollRun;
import com.payflow.payrollsystem.model.PayrollRunApproval;
import com.payflow.payrollsystem.model.Employee;
import com.payflow.payrollsystem.model.Salary;
import com.payflow.payrollsystem.model.Payslip;
import com.payflow.payrollsystem.service.PayrollService;
import com.payflow.payrollsystem.service.PayslipPdfService;
import com.payflow.payrollsystem.service.PayrollApprovalService;
import com.payflow.payrollsystem.repository.PayrollRunRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;
    private final PayslipPdfService payslipPdfService;
    private final PayrollApprovalService payrollApprovalService;

    public PayrollController(PayrollService payrollService, PayslipPdfService payslipPdfService, PayrollApprovalService payrollApprovalService) {
        this.payrollService = payrollService;
        this.payslipPdfService = payslipPdfService;
        this.payrollApprovalService = payrollApprovalService;
    }

    @GetMapping("/employees/{companyId}")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<List<Employee>> getEmployees(@PathVariable Long companyId) {
        return ResponseEntity.ok(payrollService.getEmployeesByCompany(companyId));
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(payrollService.getAllEmployees());
    }

    @PostMapping("/employees")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(payrollService.addEmployee(employee));
    }

    @PostMapping("/salaries")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<Salary> addSalary(@RequestBody Salary salary) {
        return ResponseEntity.ok(payrollService.addSalary(salary));
    }

    @PostMapping("/payslips/{employeeId}")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<Payslip> generatePayslip(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.generatePayslip(employeeId));
    }

    @GetMapping("/payslips/{payslipId}/pdf")
    @PreAuthorize("hasAnyRole('HR','FINANCE','EMPLOYEE')")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long payslipId) {
        byte[] pdf = payslipPdfService.generatePayslipPdf(payslipId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payslip.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/runs/{id}/submit")
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<PayrollRun> submitRun(@PathVariable Long id) {
        return ResponseEntity.ok(payrollApprovalService.submitForReview(id));
    }

    @PostMapping("/runs/{id}/approve")
    @PreAuthorize("hasAnyRole('FINANCE')")
    public ResponseEntity<PayrollRun> approveRun(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(payrollApprovalService.approveRun(id, comment));
    }

    @PostMapping("/runs/{id}/reject")
    @PreAuthorize("hasAnyRole('FINANCE')")
    public ResponseEntity<PayrollRun> rejectRun(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return ResponseEntity.ok(payrollApprovalService.rejectRun(id, comment));
    }

    @GetMapping("/runs/{id}/approvals")
    @PreAuthorize("hasAnyRole('HR','FINANCE')")
    public ResponseEntity<List<PayrollRunApproval>> listApprovals(@PathVariable Long id) {
        return ResponseEntity.ok(payrollApprovalService.listApprovals(id));
    }
}