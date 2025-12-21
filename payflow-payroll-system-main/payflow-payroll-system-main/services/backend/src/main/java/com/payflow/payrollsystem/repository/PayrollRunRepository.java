package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {

    List<PayrollRun> findByCompanyId(Long companyId);
}