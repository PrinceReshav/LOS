package com.los.loanoriginatingsystem.applicant.controller;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applicants")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService service;

    @GetMapping("/{applicantId}")
    public LoanApplicant getApplicant(
            @PathVariable String applicantId
    ) {
        return service.getApplicant(
                applicantId
        );
    }

    @GetMapping("/loan/{loanId}")
    public List<LoanApplicant> getByLoan(
            @PathVariable String loanId
    ) {
        return service.getApplicantsByLoan(
                loanId
        );
    }


}