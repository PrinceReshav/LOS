package com.los.loanoriginatingsystem.applicant.service;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.applicant.repository.LoanApplicantRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicantService {

    private final LoanApplicantRepository repository;

    public LoanApplicant getApplicant(
            String applicantId
    ) {
        return repository.findById(applicantId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Applicant not found"
                        )
                );
    }

    public List<LoanApplicant> getApplicantsByLoan(
            String loanId
    ) {
        return repository.findByLoanApplicationId(
                loanId
        );
    }


}