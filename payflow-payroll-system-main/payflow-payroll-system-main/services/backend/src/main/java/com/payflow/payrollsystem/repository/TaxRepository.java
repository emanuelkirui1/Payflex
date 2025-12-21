package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxRepository extends JpaRepository<Tax, Long> {
}