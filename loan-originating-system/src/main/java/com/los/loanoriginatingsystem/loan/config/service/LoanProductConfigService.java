package com.los.loanoriginatingsystem.loan.config.service;

import com.los.loanoriginatingsystem.loan.config.entity.LoanEligibilityConfig;
import com.los.loanoriginatingsystem.loan.config.repository.LoanEligibilityConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanProductConfigService {

    private final LoanEligibilityConfigRepository repo;

    public LoanEligibilityConfig findMatchingConfig(
            String productCode,
            String loanType,
            String scheme,
            BigDecimal amount,
            Integer tenure,
            Integer creditScore
    ) {

        List<LoanEligibilityConfig> configs =
                repo.findByProductCodeAndLoanTypeAndLoanScheme(
                        productCode,
                        loanType,
                        scheme
                );

        return configs.stream()
                .filter(c ->
                        amount.compareTo(c.getMinAmount()) >= 0 &&
                                amount.compareTo(c.getMaxAmount()) <= 0 &&
                                tenure >= c.getMinTenure() &&
                                tenure <= c.getMaxTenure() &&
                                creditScore >= c.getMinCreditScore() &&
                                creditScore <= c.getMaxCreditScore()
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No matching loan configuration found")
                );
    }
}