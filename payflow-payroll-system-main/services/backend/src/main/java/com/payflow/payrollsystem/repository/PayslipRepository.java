package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
}