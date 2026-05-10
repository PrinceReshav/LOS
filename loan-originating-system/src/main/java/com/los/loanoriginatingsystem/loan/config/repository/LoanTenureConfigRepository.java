package com.los.loanoriginatingsystem.loan.config.repository;

import com.los.loanoriginatingsystem.loan.config.entity.LoanTenureConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanTenureConfigRepository
        extends JpaRepository<LoanTenureConfig, String> {
}