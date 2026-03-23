package com.los.loanoriginatingsystem.loan.repository;

import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanApplicationRepository
        extends JpaRepository<LoanApplication, String> {

    Optional<LoanApplication> findByAccountAggregatorFileNo(String fileNo);
}