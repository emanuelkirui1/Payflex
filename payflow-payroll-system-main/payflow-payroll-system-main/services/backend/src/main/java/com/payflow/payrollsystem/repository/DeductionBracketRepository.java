package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.DeductionBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeductionBracketRepository extends JpaRepository<DeductionBracket, Long> {

    @Query("SELECT db FROM DeductionBracket db WHERE db.type = :type AND db.effectiveFrom <= :date AND (db.effectiveTo IS NULL OR db.effectiveTo > :date) ORDER BY db.minSalary")
    List<DeductionBracket> findActiveBracketsByType(String type, LocalDate date);
}