package com.los.loanoriginatingsystem.loan.config.repository;

import com.los.loanoriginatingsystem.loan.config.entity.LoanEligibilityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanEligibilityConfigRepository
        extends JpaRepository<LoanEligibilityConfig, String> {

    List<LoanEligibilityConfig> findByProductCodeAndLoanTypeAndLoanScheme(
            String productCode,
            String loanType,
            String loanScheme
    );
}