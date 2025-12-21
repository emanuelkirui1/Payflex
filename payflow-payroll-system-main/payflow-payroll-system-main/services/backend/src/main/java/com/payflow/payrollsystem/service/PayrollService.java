package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.*;
import com.payflow.payrollsystem.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final TaxRepository taxRepository;
    private final PayslipRepository payslipRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final TaxCalculationService taxCalculationService;
    private final DeductionService deductionService;
    private final PensionService pensionService;

    public PayrollService(EmployeeRepository employeeRepository, SalaryRepository salaryRepository,
                          TaxRepository taxRepository, PayslipRepository payslipRepository,
                          PayrollRunRepository payrollRunRepository, AuditLogRepository auditLogRepository, UserRepository userRepository, TaxCalculationService taxCalculationService,
                          DeductionService deductionService, PensionService pensionService) {
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.taxRepository = taxRepository;
        this.payslipRepository = payslipRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.taxCalculationService = taxCalculationService;
        this.deductionService = deductionService;
        this.pensionService = pensionService;
    }

    private void logAudit(String action) {
        logAudit(action, null, null, null);
    }

    private void logAudit(String action, String entityType, Long entityId, String metadata) {
        String email = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        if (entityType != null) log.setEntityType(entityType);
        if (entityId != null) log.setEntityId(entityId);
        if (metadata != null) log.setMetadata(metadata);
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(log::setUser);
        }
        auditLogRepository.save(log);
    }
    public List<Employee> getEmployeesByCompany(Long companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee addEmployee(Employee employee) {
        Employee saved = employeeRepository.save(employee);
        logAudit("Employee created: " + employee.getFirstName() + " " + employee.getLastName());
        return saved;
    }

    public Salary addSalary(Salary salary) {
        return salaryRepository.save(salary);
    }

    public Tax calculateTax(Salary salary) {
        BigDecimal taxableIncome = salary.getTaxableIncome();
        BigDecimal taxAmount = taxCalculationService.calculatePAYE(taxableIncome);
        Tax tax = new Tax();
        tax.setSalary(salary);
        tax.setTaxAmount(taxAmount);
        tax.setCreatedAt(LocalDateTime.now());
        return taxRepository.save(tax);
    }

    private BigDecimal calculateProgressiveTax(BigDecimal gross) {
        // This is now handled by TaxCalculationService
        return taxCalculationService.calculatePAYE(gross);
    }

    // single logAudit overloads are defined above

    public Payslip generatePayslip(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        // Assume latest salary
        Salary salary = employee.getSalaries().get(employee.getSalaries().size() - 1);
        BigDecimal basic = salary.getBasicSalary();
        BigDecimal allowances = salary.getAllowances() != null ? salary.getAllowances() : BigDecimal.ZERO;
        BigDecimal gross = basic.add(allowances);
        salary.setGrossSalary(gross);

        // Calculate deductions
        BigDecimal nssf = pensionService.calculateNSSF(basic);
        BigDecimal nhif = deductionService.calculateNHIF(gross);
        BigDecimal pension = nssf; // Assuming NSSF is the pension

        // Taxable income = gross - nssf (as per Kenya rules, NSSF reduces taxable income)
        BigDecimal taxableIncome = gross.subtract(nssf);
        BigDecimal paye = taxCalculationService.calculatePAYE(taxableIncome);

        BigDecimal totalDeductions = paye.add(nssf).add(nhif);
        BigDecimal netPay = gross.subtract(totalDeductions);

        // Update salary with calculations
        salary.setNssfDeduction(nssf);
        salary.setNhifDeduction(nhif);
        salary.setPensionContribution(pension);
        salary.setTaxableIncome(taxableIncome);
        salary.setPayeTax(paye);
        salary.setTotalDeductions(totalDeductions);
        salary.setNetPay(netPay);
        salaryRepository.save(salary);

        // Create tax record
        Tax tax = new Tax();
        tax.setSalary(salary);
        tax.setTaxAmount(paye);
        tax.setCreatedAt(LocalDateTime.now());
        taxRepository.save(tax);

        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setNetPay(netPay);
        payslip.setGeneratedAt(LocalDateTime.now());
        Payslip saved = payslipRepository.save(payslip);
        logAudit("Payslip generated for employee: " + employee.getFirstName() + " " + employee.getLastName());
        return saved;
    }
}