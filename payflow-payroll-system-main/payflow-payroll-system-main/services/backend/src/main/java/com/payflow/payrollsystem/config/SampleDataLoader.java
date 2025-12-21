package com.payflow.payrollsystem.config;

import com.payflow.payrollsystem.model.*;
import com.payflow.payrollsystem.repository.*;
import com.payflow.payrollsystem.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile("default")
public class SampleDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final PayslipRepository payslipRepository;
    private final CompanyRepository companyRepository;

    public SampleDataLoader(UserRepository userRepository,
                            EmployeeRepository employeeRepository,
                            SalaryRepository salaryRepository,
                            PayrollRunRepository payrollRunRepository,
                            PayslipRepository payslipRepository,
                            CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.payslipRepository = payslipRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only add sample data if there are no employees yet
        if (employeeRepository.count() > 0) return;

        // Ensure users exist
        User johnUser = createUserIfAbsent("john.doe@test.com", "EMPLOYEE");
        User janeUser = createUserIfAbsent("jane.smith@test.com", "EMPLOYEE");
        User bobUser = createUserIfAbsent("bob.williams@test.com", "EMPLOYEE");

        // Create employees
        Employee john = new Employee();
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setKraPin("KRA123456");
        john.setCompany(johnUser.getCompany());
        john.setUser(johnUser);
        john.setCreatedAt(LocalDateTime.now());
        john = employeeRepository.save(john);

        Employee jane = new Employee();
        jane.setFirstName("Jane");
        jane.setLastName("Smith");
        jane.setKraPin("KRA654321");
        jane.setCompany(janeUser.getCompany());
        jane.setUser(janeUser);
        jane.setCreatedAt(LocalDateTime.now());
        jane = employeeRepository.save(jane);

        Employee bob = new Employee();
        bob.setFirstName("Bob");
        bob.setLastName("Williams");
        bob.setKraPin("KRA999999");
        bob.setCompany(bobUser.getCompany());
        bob.setUser(bobUser);
        bob.setCreatedAt(LocalDateTime.now());
        bob = employeeRepository.save(bob);

        // Create salaries
        Salary s1 = new Salary();
        s1.setAllowances(new BigDecimal("20000"));
        s1.setBasicSalary(new BigDecimal("100000"));
        s1.setCreatedAt(LocalDateTime.now());
        s1.setGrossSalary(new BigDecimal("120000"));
        s1.setNetPay(new BigDecimal("100000"));
        s1.setNhifDeduction(new BigDecimal("2000"));
        s1.setNssfDeduction(new BigDecimal("5000"));
        s1.setPayeTax(new BigDecimal("13000"));
        s1.setTaxableIncome(new BigDecimal("105000"));
        s1.setTotalDeductions(new BigDecimal("20000"));
        s1.setEmployee(john);
        salaryRepository.save(s1);

        Salary s2 = new Salary();
        s2.setAllowances(new BigDecimal("10000"));
        s2.setBasicSalary(new BigDecimal("80000"));
        s2.setCreatedAt(LocalDateTime.now());
        s2.setGrossSalary(new BigDecimal("90000"));
        s2.setNetPay(new BigDecimal("75000"));
        s2.setNhifDeduction(new BigDecimal("1500"));
        s2.setNssfDeduction(new BigDecimal("4000"));
        s2.setPayeTax(new BigDecimal("12500"));
        s2.setTaxableIncome(new BigDecimal("82000"));
        s2.setTotalDeductions(new BigDecimal("15000"));
        s2.setEmployee(jane);
        salaryRepository.save(s2);

        Salary s3 = new Salary();
        s3.setAllowances(new BigDecimal("5000"));
        s3.setBasicSalary(new BigDecimal("50000"));
        s3.setCreatedAt(LocalDateTime.now());
        s3.setGrossSalary(new BigDecimal("55000"));
        s3.setNetPay(new BigDecimal("45000"));
        s3.setNhifDeduction(new BigDecimal("1000"));
        s3.setNssfDeduction(new BigDecimal("3000"));
        s3.setPayeTax(new BigDecimal("8000"));
        s3.setTaxableIncome(new BigDecimal("49500"));
        s3.setTotalDeductions(new BigDecimal("10000"));
        s3.setEmployee(bob);
        salaryRepository.save(s3);

        // Create a draft payroll run
        PayrollRun run = new PayrollRun();
        run.setCreatedAt(LocalDateTime.now());
        run.setCreatedBy("super@test.com");
        run.setRunDate(LocalDateTime.now());
        run.setStatus("DRAFT");
        run.setCompany(john.getCompany());
        run.setTotalGross(s1.getGrossSalary().add(s2.getGrossSalary()).add(s3.getGrossSalary()));
        run.setTotalNet(s1.getNetPay().add(s2.getNetPay()).add(s3.getNetPay()));
        run.setTotalDeductions(s1.getTotalDeductions().add(s2.getTotalDeductions()).add(s3.getTotalDeductions()));
        run = payrollRunRepository.save(run);

        // Create payslips
        Payslip p1 = new Payslip();
        p1.setGeneratedAt(LocalDateTime.now());
        p1.setNetPay(s1.getNetPay());
        p1.setEmployee(john);
        p1.setPayrollRun(run);
        payslipRepository.save(p1);

        Payslip p2 = new Payslip();
        p2.setGeneratedAt(LocalDateTime.now());
        p2.setNetPay(s2.getNetPay());
        p2.setEmployee(jane);
        p2.setPayrollRun(run);
        payslipRepository.save(p2);

        Payslip p3 = new Payslip();
        p3.setGeneratedAt(LocalDateTime.now());
        p3.setNetPay(s3.getNetPay());
        p3.setEmployee(bob);
        p3.setPayrollRun(run);
        payslipRepository.save(p3);
    }

    private User createUserIfAbsent(String email, String role) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) return existing.get();
        User u = new User();
        u.setEmail(email);
        u.setPassword("password");
        u.setRole(role);
        u.setEnabled(true);
        u.setCreatedAt(LocalDateTime.now());
        // link to first company (Test Company) if available
        companyRepository.findAll().stream().findFirst().ifPresent(u::setCompany);
        userRepository.save(u);
        // reload to get any DB-side defaults
        return userRepository.findByEmail(email).orElse(u);
    }
}
