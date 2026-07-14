package com.los.loanoriginatingsystem.loan.service;

import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanApplicationStageService {

    private final LoanApplicationRepository repository;

    public LoanApplication advanceStage(String loanId) {

        LoanApplication loan =
                repository.findById(loanId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Loan Application not found : " + loanId
                                )
                        );

        LoanStage currentStage =
                parseStage(loan.getStage());

        LoanStage nextStage =
                currentStage.next();

        loan.setStage(nextStage.name());

        repository.save(loan);

        return loan;
    }

    private LoanStage parseStage(String stage) {

        if (stage == null || stage.isBlank()) {
            return LoanStage.DATA_ENTRY;
        }

        try {
            return LoanStage.valueOf(stage);
        } catch (IllegalArgumentException e) {
            return LoanStage.DATA_ENTRY;
        }
    }
}
