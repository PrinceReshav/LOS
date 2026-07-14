package com.los.loanoriginatingsystem.applicant.repository;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicantRepository
        extends JpaRepository<LoanApplicant, String> {
    List<LoanApplicant> findByLoanApplicationId(
            String loanApplicationId
    );
}