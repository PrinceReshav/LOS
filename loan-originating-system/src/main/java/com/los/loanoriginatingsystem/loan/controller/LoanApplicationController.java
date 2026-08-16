package com.los.loanoriginatingsystem.loan.controller;

import com.los.loanoriginatingsystem.access.client.RecordAccessClient;
import com.los.loanoriginatingsystem.access.dto.RecordAccessLevel;
import com.los.loanoriginatingsystem.access.dto.RecordAccessScopeResponse;
import com.los.loanoriginatingsystem.loan.dto.RejectLoanRequest;
import com.los.loanoriginatingsystem.loan.dto.StageHistoryEntry;
import com.los.loanoriginatingsystem.loan.dto.StageTransitionRequest;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.service.LoanApplicationStageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private static final String RECORD_TYPE = "LOAN_APPLICATION";

    private final LoanApplicationRepository repository;
    private final LoanApplicationStageService stageService;
    private final RecordAccessClient recordAccessClient;

    /**
     * Scoped to what the caller is allowed to see - resolved from
     * administration-service (role-hierarchy subordinates' records + own
     * branch + any explicit shares), not a blanket findAll(). See
     * RecordAccessService.scope() in administration-service for the rules.
     */
    @GetMapping
    public List<LoanApplication> getAll() {

        String currentUserId = currentUserId();
        RecordAccessScopeResponse scope = recordAccessClient.getScope(currentUserId, RECORD_TYPE);

        if (scope.isSeesAll()) {
            return repository.findAll();
        }

        return repository.findByCreatedByInOrBranchIdInOrIdIn(
                scope.getVisibleOwnerUserIds() != null ? scope.getVisibleOwnerUserIds() : List.of(),
                scope.getVisibleBranchIds() != null ? scope.getVisibleBranchIds() : List.of(),
                scope.getVisibleRecordIds() != null ? scope.getVisibleRecordIds() : List.of()
        );
    }

    @GetMapping("/{loanId}")
    public LoanApplication getById(
            @PathVariable String loanId
    ) {

        LoanApplication loan = repository.findById(loanId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Loan Application not found"
                        )
                );

        String currentUserId = currentUserId();
        boolean allowed = recordAccessClient.canAccess(
                currentUserId, RECORD_TYPE, loanId, loan.getCreatedBy(), loan.getBranchId(), RecordAccessLevel.READ);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this loan application");
        }

        return loan;
    }

    /**
     * Moves the application to targetStage - validated against the
     * DATA_ENTRY -> UNDERWRITING -> PRE_SANCTION -> SANCTION ->
     * PRE_DISBURSAL_REVIEW -> INITIATE_DISBURSEMENT -> DISBURSED pipeline
     * (see LoanStageTransitionRules) and against real eligibility guards
     * (see LoanStageGuardService) - not just any string the client sends.
     */
    @PostMapping("/{loanId}/stage/transition")
    public LoanApplication transitionStage(
            @PathVariable String loanId,
            @Valid @RequestBody StageTransitionRequest request
    ) {
        assertWriteAccess(loanId);
        return stageService.transitionTo(loanId, request.getTargetStage(), request.getRemarks());
    }

    /** Rejects the application from whatever non-terminal stage it's currently in. Remarks are mandatory. */
    @PostMapping("/{loanId}/stage/reject")
    public LoanApplication rejectStage(
            @PathVariable String loanId,
            @Valid @RequestBody RejectLoanRequest request
    ) {
        assertWriteAccess(loanId);
        return stageService.reject(loanId, request);
    }

    @GetMapping("/{loanId}/stage/allowed-transitions")
    public Set<LoanStage> allowedNextStages(
            @PathVariable String loanId
    ) {
        return stageService.getAllowedNextStages(loanId);
    }

    @GetMapping("/{loanId}/stage/history")
    public List<StageHistoryEntry> stageHistory(
            @PathVariable String loanId
    ) {
        return stageService.getHistory(loanId);
    }

    // ==========================================================

    private void assertWriteAccess(String loanId) {
        LoanApplication loan = repository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan Application not found"));

        String currentUserId = currentUserId();
        boolean allowed = recordAccessClient.canAccess(
                currentUserId, RECORD_TYPE, loanId, loan.getCreatedBy(), loan.getBranchId(), RecordAccessLevel.READ_WRITE);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have write access to this loan application");
        }
    }

    private String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
