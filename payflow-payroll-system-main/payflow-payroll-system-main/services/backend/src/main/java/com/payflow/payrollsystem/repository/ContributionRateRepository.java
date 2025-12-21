package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.ContributionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ContributionRateRepository extends JpaRepository<ContributionRate, Long> {

    @Query("SELECT cr FROM ContributionRate cr WHERE cr.type = :type AND cr.effectiveFrom <= :date AND (cr.effectiveTo IS NULL OR cr.effectiveTo > :date)")
    Optional<ContributionRate> findActiveRateByType(String type, LocalDate date);
}