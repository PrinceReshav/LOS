package com.los.loanoriginatingsystem.loanProduct.config.repository;

import com.los.loanoriginatingsystem.loanProduct.config.entity.LoanEligibilityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanProductEligibilityConfigRepository
        extends JpaRepository<LoanEligibilityConfig, String> {

    List<LoanEligibilityConfig> findByProductCodeAndLoanTypeAndLoanScheme(
            String productCode,
            String loanType,
            String loanScheme
    );
}