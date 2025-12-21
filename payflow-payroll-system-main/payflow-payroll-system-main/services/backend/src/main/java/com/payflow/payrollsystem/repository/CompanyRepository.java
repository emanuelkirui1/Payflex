package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}