package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.PayrollRunApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRunApprovalRepository extends JpaRepository<PayrollRunApproval, Long> {
    List<PayrollRunApproval> findByPayrollRunId(Long payrollRunId);
}
