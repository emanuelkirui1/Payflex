package com.payflow.payrollsystem.service;

import com.payflow.payrollsystem.model.ContributionRate;
import com.payflow.payrollsystem.repository.ContributionRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PensionService {

    private final ContributionRateRepository contributionRateRepository;

    public PensionService(ContributionRateRepository contributionRateRepository) {
        this.contributionRateRepository = contributionRateRepository;
    }

    public BigDecimal calculateNSSF(BigDecimal basicSalary) {
        Optional<ContributionRate> rateOpt = contributionRateRepository.findActiveRateByType("NSSF_EMPLOYEE", LocalDate.now());
        if (rateOpt.isPresent()) {
            ContributionRate rate = rateOpt.get();
            BigDecimal pensionable = basicSalary.min(rate.getMaxPensionableEarnings());
            return pensionable.multiply(rate.getRate());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateEmployerNSSF(BigDecimal basicSalary) {
        Optional<ContributionRate> rateOpt = contributionRateRepository.findActiveRateByType("NSSF_EMPLOYER", LocalDate.now());
        if (rateOpt.isPresent()) {
            ContributionRate rate = rateOpt.get();
            BigDecimal pensionable = basicSalary.min(rate.getMaxPensionableEarnings());
            return pensionable.multiply(rate.getRate());
        }
        return BigDecimal.ZERO;
    }
}