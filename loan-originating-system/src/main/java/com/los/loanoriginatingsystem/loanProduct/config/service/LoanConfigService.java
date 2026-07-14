package com.los.loanoriginatingsystem.loanProduct.config.service;

import com.los.loanoriginatingsystem.loanProduct.config.entity.LoanEligibilityConfig;
import com.los.loanoriginatingsystem.loanProduct.config.entity.LoanTenureConfig;
import com.los.loanoriginatingsystem.loanProduct.config.repository.LoanProductEligibilityConfigRepository;
import com.los.loanoriginatingsystem.loanProduct.config.repository.LoanProductTenureConfigRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanConfigService {

    private final LoanProductEligibilityConfigRepository eligibilityRepo;
    private final LoanProductTenureConfigRepository tenureRepo;

    // =====================================================
    // 🔥 MATCH ELIGIBILITY CONFIG
    // =====================================================
    public LoanEligibilityConfig findMatchingConfig(
            String productCode,
            String loanType,
            String loanScheme,
            BigDecimal amount,
            Integer tenure,
            Integer creditScore
    ) {

        List<LoanEligibilityConfig> configs =
                eligibilityRepo.findByProductCodeAndLoanTypeAndLoanScheme(
                        productCode,
                        loanType,
                        loanScheme
                );

        return configs.stream()
                .filter(c ->
                        isEligible(c, amount, tenure, creditScore)
                )
                .findFirst()
                .orElse(null);
    }

    private boolean isEligible(
            LoanEligibilityConfig c,
            BigDecimal amount,
            Integer tenure,
            Integer creditScore
    ) {

        return amount.compareTo(c.getMinLoanAmount()) >= 0 &&
                amount.compareTo(c.getMaxLoanAmount()) <= 0 &&

                tenure >= c.getMinTenure() &&
                tenure <= c.getMaxTenure() &&

                creditScore >= c.getMinCreditScore() &&
                creditScore <= c.getMaxCreditScore();
    }

    // =====================================================
    // 🔥 TENURE VALIDATION (OPTIONAL EXTENSION)
    // =====================================================
    public boolean isValidTenure(BigDecimal amount, Integer tenure) {

        List<LoanTenureConfig> configs = tenureRepo.findAll();

        return configs.stream()
                .anyMatch(c ->
                        amount.compareTo(c.getMinAmount()) >= 0 &&
                                amount.compareTo(c.getMaxAmount()) <= 0 &&
                                tenure >= c.getMinTenure() &&
                                tenure <= c.getMaxTenure()
                );
    }
}