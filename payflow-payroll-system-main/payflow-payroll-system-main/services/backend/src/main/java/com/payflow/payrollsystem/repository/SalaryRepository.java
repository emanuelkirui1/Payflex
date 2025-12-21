package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
}