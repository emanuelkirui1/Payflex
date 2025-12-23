package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.LeaveRequestApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestApprovalRepository extends JpaRepository<LeaveRequestApproval, Long> {
    List<LeaveRequestApproval> findByLeaveRequestId(Long leaveRequestId);
}
