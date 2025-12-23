package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.DeductionBracket;
import com.payflow.payrollsystem.repository.DeductionBracketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DeductionService {

    private final DeductionBracketRepository deductionBracketRepository;

    public DeductionService(DeductionBracketRepository deductionBracketRepository) {
        this.deductionBracketRepository = deductionBracketRepository;
    }

    public BigDecimal calculateNHIF(BigDecimal grossSalary) {
        List<DeductionBracket> brackets = deductionBracketRepository.findActiveBracketsByType("NHIF", LocalDate.now());
        for (DeductionBracket bracket : brackets) {
            if (grossSalary.compareTo(bracket.getMaxSalary()) <= 0) {
                return bracket.getDeductionAmount();
            }
        }
        // If above highest bracket, return the highest amount
        return brackets.isEmpty() ? BigDecimal.ZERO : brackets.get(brackets.size() - 1).getDeductionAmount();
    }
}