package com.los.loanoriginatingsystem.loan.workflow;

import com.los.loanoriginatingsystem.commercial.repository.CommercialApprovalRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentChecklistRepository;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import com.los.loanoriginatingsystem.rules.service.DeviationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Guard conditions for each stage transition - "is this application
 * actually eligible to move right now", as opposed to
 * LoanStageTransitionRules which only answers "is this jump shape-legal at
 * all". A transition can be graph-legal and still blocked here.
 *
 * Deliberately built on top of services that already exist (Commercial
 * Approval, Deviation engine, Document Checklist) rather than duplicating
 * their logic - each guard is a thin read-only check.
 */
@Service
@RequiredArgsConstructor
public class LoanStageGuardService {

    private final CommercialApprovalRepository commercialApprovalRepository;
    private final DeviationService deviationService;
    private final DocumentChecklistRepository documentChecklistRepository;
    private final DocumentRepository documentRepository;

    /**
     * @return null if the transition is allowed right now, or a human-readable
     *         reason why it's blocked.
     */
    public String checkGuard(LoanApplication app, LoanStage from, LoanStage to) {

        if (to == LoanStage.REJECTED) {
            return null; // rejection has no eligibility guard - only the remarks requirement, enforced by the caller
        }

        return switch (to) {
            case UNDERWRITING -> guardEnterUnderwriting(app);
            case PRE_SANCTION -> guardEnterPreSanction(app);
            case SANCTION -> guardEnterSanction(app);
            case PRE_DISBURSAL_REVIEW -> guardEnterPreDisbursalReview(app);
            case INITIATE_DISBURSEMENT -> guardEnterInitiateDisbursement(app);
            case DISBURSED -> guardEnterDisbursed(app);
            default -> null;
        };
    }

    private String guardEnterUnderwriting(LoanApplication app) {
        if (app.getPrimaryApplicantId() == null || app.getPrimaryApplicantId().isBlank()) {
            return "Primary applicant must be set before moving to Underwriting";
        }
        return null;
    }

    private String guardEnterPreSanction(LoanApplication app) {
        long pendingDeviations = deviationService.countPending(app.getId());
        if (pendingDeviations > 0) {
            return "Cannot move to Pre-Sanction: " + pendingDeviations + " deviation(s) still pending resolution";
        }
        return null;
    }

    private String guardEnterSanction(LoanApplication app) {
        return commercialApprovalRepository.findByLoanApplicationId(app.getId())
                .map(approval -> {
                    String status = approval.getStatus();
                    if ("APPROVED".equals(status) || "AUTO_APPROVED".equals(status)) {
                        return null;
                    }
                    return "Cannot move to Sanction: commercial approval status is " + status
                            + " (must be APPROVED or AUTO_APPROVED)";
                })
                .orElse("Cannot move to Sanction: commercial approval has not been resolved yet");
    }

    private String guardEnterPreDisbursalReview(LoanApplication app) {
        return missingRequiredDocuments(app);
    }

    private String guardEnterInitiateDisbursement(LoanApplication app) {
        return null; // extension point: bank-account-verification / e-NACH-mandate-active checks go here once built
    }

    private String guardEnterDisbursed(LoanApplication app) {
        return null; // extension point: LMS disbursement-confirmation check goes here once LMS sync is built
    }

    private String missingRequiredDocuments(LoanApplication app) {

        if (app.getLoanProductCode() == null) {
            return null; // nothing to check against
        }

        Set<String> required = documentChecklistRepository
                .findByLoanProductCodeAndActiveTrue(app.getLoanProductCode())
                .stream()
                .map(c -> c.getDocumentType())
                .collect(java.util.stream.Collectors.toSet());

        if (required.isEmpty()) {
            return null;
        }

        List<Document> uploaded = documentRepository.findByLoanApplicationId(app.getId());
        Set<String> uploadedTypes = uploaded.stream().map(Document::getDocumentType).collect(java.util.stream.Collectors.toSet());

        Set<String> missing = new java.util.HashSet<>(required);
        missing.removeAll(uploadedTypes);

        if (missing.isEmpty()) {
            return null;
        }

        return "Cannot proceed: missing required document(s): " + String.join(", ", missing);
    }
}
