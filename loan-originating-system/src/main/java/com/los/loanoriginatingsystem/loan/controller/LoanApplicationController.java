package com.los.loanoriginatingsystem.loan.controller;

import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.service.LoanApplicationStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationRepository repository;
    private final LoanApplicationStageService stageService;

    @GetMapping
    public List<LoanApplication> getAll() {

        return repository.findAll();
    }

    @GetMapping("/{loanId}")
    public LoanApplication getById(
            @PathVariable String loanId
    ) {

        return repository.findById(loanId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Loan Application not found"
                        )
                );
    }

    // Advances the application to the next stage in the fixed
    // DATA_ENTRY -> UNDERWRITING -> PRE_SANCTION -> SANCTION ->
    // DISBURSEMENT pipeline.
    @PostMapping("/{loanId}/advance-stage")
    public LoanApplication advanceStage(
            @PathVariable String loanId
    ) {

        return stageService.advanceStage(loanId);
    }
}
