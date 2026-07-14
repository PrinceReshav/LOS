package com.los.loanoriginatingsystem.loanProduct.config.repository;

import com.los.loanoriginatingsystem.loanProduct.config.entity.LoanTenureConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductTenureConfigRepository
        extends JpaRepository<LoanTenureConfig, String> {
}