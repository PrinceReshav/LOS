package com.los.loanoriginatingsystem.commercial.controller;

import com.los.loanoriginatingsystem.commercial.dto.CommercialApprovalActionRequest;
import com.los.loanoriginatingsystem.commercial.dto.CommercialApprovalResolveRequest;
import com.los.loanoriginatingsystem.commercial.entity.CommercialApproval;
import com.los.loanoriginatingsystem.commercial.service.CommercialApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Drives the commercial-approval workflow for a single loan application:
 * resolve (run the matrix + assign approvers) -> submit -> approve/reject.
 */
@RestController
@RequestMapping("/api/loan-applications/{loanApplicationId}/commercial-approval")
@RequiredArgsConstructor
public class CommercialApprovalController {

    private final CommercialApprovalService service;

    @GetMapping
    public CommercialApproval get(@PathVariable String loanApplicationId) {
        return service.get(loanApplicationId);
    }

    @PostMapping("/resolve")
    public CommercialApproval resolve(
            @PathVariable String loanApplicationId,
            @RequestBody(required = false) CommercialApprovalResolveRequest request
    ) {
        return service.resolve(loanApplicationId, request != null ? request : new CommercialApprovalResolveRequest());
    }

    @PostMapping("/submit")
    public CommercialApproval submit(@PathVariable String loanApplicationId) {
        return service.submit(loanApplicationId);
    }

    @PostMapping("/approve")
    public CommercialApproval approve(
            @PathVariable String loanApplicationId,
            @Valid @RequestBody CommercialApprovalActionRequest request
    ) {
        return service.approve(loanApplicationId, request);
    }

    @PostMapping("/reject")
    public CommercialApproval reject(
            @PathVariable String loanApplicationId,
            @Valid @RequestBody CommercialApprovalActionRequest request
    ) {
        return service.reject(loanApplicationId, request);
    }
}
