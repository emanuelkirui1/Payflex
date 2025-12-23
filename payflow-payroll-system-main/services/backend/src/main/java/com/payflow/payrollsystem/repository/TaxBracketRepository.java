package com.payflow.payrollsystem.repository;

import com.payflow.payrollsystem.model.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    @Query("SELECT tb FROM TaxBracket tb WHERE tb.effectiveFrom <= :date AND (tb.effectiveTo IS NULL OR tb.effectiveTo > :date) ORDER BY tb.minAmount")
    List<TaxBracket> findActiveBrackets(LocalDate date);
}