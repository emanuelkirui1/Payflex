package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.TaxBracket;
import com.payflow.payrollsystem.repository.TaxBracketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaxCalculationService {

    private final TaxBracketRepository taxBracketRepository;

    public TaxCalculationService(TaxBracketRepository taxBracketRepository) {
        this.taxBracketRepository = taxBracketRepository;
    }

    public BigDecimal calculatePAYE(BigDecimal taxableIncome) {
        List<TaxBracket> brackets = taxBracketRepository.findActiveBrackets(LocalDate.now());
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal personalRelief = new BigDecimal("2400");

        for (TaxBracket bracket : brackets) {
            if (taxableIncome.compareTo(bracket.getMaxAmount()) > 0) {
                tax = tax.add(bracket.getMaxAmount().subtract(bracket.getMinAmount()).multiply(bracket.getRate()));
            } else {
                tax = tax.add(taxableIncome.subtract(bracket.getMinAmount()).multiply(bracket.getRate()));
                break;
            }
        }
        return tax.subtract(personalRelief).max(BigDecimal.ZERO);
    }
}