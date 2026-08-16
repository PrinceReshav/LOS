package com.los.loanoriginatingsystem.loan.service;

import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import com.los.loanoriginatingsystem.audit.repository.ActionAuditRepository;
import com.los.loanoriginatingsystem.audit.service.AuditService;
import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.loan.dto.RejectLoanRequest;
import com.los.loanoriginatingsystem.loan.dto.StageHistoryEntry;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.workflow.LoanStageGuardService;
import com.los.loanoriginatingsystem.loan.workflow.LoanStageSideEffectService;
import com.los.loanoriginatingsystem.loan.workflow.LoanStageTransitionRules;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Drives the loan application's primary stage workflow:
 * DATA_ENTRY -> UNDERWRITING -> PRE_SANCTION -> SANCTION ->
 * PRE_DISBURSAL_REVIEW -> INITIATE_DISBURSEMENT -> DISBURSED,
 * with REJECTED reachable from any non-terminal stage.
 *
 * Every transition is: graph-legal (LoanStageTransitionRules) AND
 * guard-eligible (LoanStageGuardService) -> persist -> audit
 * (reusing the existing tamper-evident ActionAudit log) -> side effects
 * (LoanStageSideEffectService, best-effort, never blocks the transition).
 */
@Service
@RequiredArgsConstructor
public class LoanApplicationStageService {

    private static final String ENTITY_TYPE = "LOAN_APPLICATION";

    private final LoanApplicationRepository repository;
    private final LoanStageGuardService guardService;
    private final LoanStageSideEffectService sideEffectService;
    private final AuditService auditService;
    private final ActionAuditRepository actionAuditRepository;

    @Transactional
    public LoanApplication transitionTo(String loanId, LoanStage targetStage, String remarks) {

        LoanApplication loan = getOrThrow(loanId);
        LoanStage currentStage = parseStage(loan.getStage());

        if (currentStage.isTerminal()) {
            throw new IllegalStateException(
                    "Loan application " + loanId + " is already in a terminal stage: " + currentStage);
        }

        if (!LoanStageTransitionRules.isAllowed(currentStage, targetStage)) {
            throw new IllegalStateException(
                    "Cannot move from " + currentStage + " to " + targetStage
                            + ". Allowed next stage(s): " + LoanStageTransitionRules.allowedNextStages(currentStage));
        }

        String guardFailureReason = guardService.checkGuard(loan, currentStage, targetStage);
        if (guardFailureReason != null) {
            throw new IllegalStateException(guardFailureReason);
        }

        return applyTransition(loan, currentStage, targetStage, remarks);
    }

    @Transactional
    public LoanApplication reject(String loanId, RejectLoanRequest request) {

        LoanApplication loan = getOrThrow(loanId);
        LoanStage currentStage = parseStage(loan.getStage());

        if (currentStage.isTerminal()) {
            throw new IllegalStateException(
                    "Loan application " + loanId + " is already in a terminal stage: " + currentStage);
        }

        loan.setRejectCode(request.getRejectCode());
        loan.setRejectReason(request.getRemarks());
        loan.setRejectedDate(LocalDateTime.now());

        return applyTransition(loan, currentStage, LoanStage.REJECTED, request.getRemarks());
    }

    public Set<LoanStage> getAllowedNextStages(String loanId) {
        LoanApplication loan = getOrThrow(loanId);
        LoanStage currentStage = parseStage(loan.getStage());
        return LoanStageTransitionRules.allowedNextStages(currentStage);
    }

    public List<StageHistoryEntry> getHistory(String loanId) {
        getOrThrow(loanId); // 404s if the loan doesn't exist
        return actionAuditRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        ENTITY_TYPE, loanId, PageRequest.of(0, 200, Sort.by("createdAt").descending()))
                .stream()
                .map(this::toHistoryEntry)
                .toList();
    }

    // ==========================================================

    private LoanApplication applyTransition(LoanApplication loan, LoanStage from, LoanStage to, String remarks) {

        loan.setStage(to.name());
        LoanApplication saved = repository.save(loan);

        auditService.log(
                ENTITY_TYPE,
                loan.getId(),
                to == LoanStage.REJECTED ? "REJECT" : "STAGE_TRANSITION",
                from.name(),
                to.name(),
                remarks
        );

        sideEffectService.onEnter(saved, to, remarks);

        return saved;
    }

    private StageHistoryEntry toHistoryEntry(ActionAudit audit) {
        return new StageHistoryEntry(
                audit.getOldStatus(),
                audit.getNewStatus(),
                audit.getPerformedBy(),
                audit.getRemarks(),
                audit.getCreatedAt()
        );
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

    private LoanApplication getOrThrow(String loanId) {
        return repository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Application not found: " + loanId));
    }
}
