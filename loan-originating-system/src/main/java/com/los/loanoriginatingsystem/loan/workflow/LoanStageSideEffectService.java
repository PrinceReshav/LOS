package com.los.loanoriginatingsystem.loan.workflow;

import com.los.loanoriginatingsystem.commercial.dto.CommercialApprovalResolveRequest;
import com.los.loanoriginatingsystem.commercial.service.CommercialApprovalService;
import com.los.loanoriginatingsystem.documentgeneration.dto.GenerateDocumentRequest;
import com.los.loanoriginatingsystem.documentgeneration.service.DocumentGenerationService;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.enums.LoanStage;
import com.los.loanoriginatingsystem.notification.client.NotificationClient;
import com.los.loanoriginatingsystem.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * What happens automatically when an application ENTERS a given stage -
 * document generation and notifications. Every call here is best-effort:
 * a side effect failing (e.g. document template misconfigured, admin
 * service briefly down for a notification) must never roll back or block
 * the stage transition itself, since the transition is the thing the
 * underwriter/ops person is actually waiting on. Failures are logged, not
 * swallowed silently.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanStageSideEffectService {

    private final CommercialApprovalService commercialApprovalService;
    private final DocumentGenerationService documentGenerationService;
    private final NotificationClient notificationClient;

    public void onEnter(LoanApplication app, LoanStage newStage, String remarks) {
        try {
            switch (newStage) {
                case PRE_SANCTION -> onEnterPreSanction(app);
                case SANCTION -> onEnterSanction(app);
                case INITIATE_DISBURSEMENT -> onEnterInitiateDisbursement(app);
                case DISBURSED -> onEnterDisbursed(app);
                case REJECTED -> onEnterRejected(app, remarks);
                default -> { /* no side effects for DATA_ENTRY / UNDERWRITING / PRE_DISBURSAL_REVIEW */ }
            }
        } catch (Exception e) {
            log.error("Stage side-effect failed for loanApplicationId={}, stage={}: {}",
                    app.getId(), newStage, e.getMessage(), e);
        }
    }

    /** Pre-resolve commercial approval as soon as the file enters Pre-Sanction, so it's ready by the time Sanction is attempted. */
    private void onEnterPreSanction(LoanApplication app) {
        try {
            commercialApprovalService.resolve(app.getId(), new CommercialApprovalResolveRequest());
        } catch (Exception e) {
            // Not fatal here - the SANCTION guard will catch an unresolved/blocked
            // approval and stop the user there with a clear reason.
            log.warn("Commercial approval auto-resolve failed on entering Pre-Sanction for {}: {}",
                    app.getId(), e.getMessage());
        }
    }

    private void onEnterSanction(LoanApplication app) {
        documentGenerationService.generate(app.getId(), "SANCTION_LETTER", new GenerateDocumentRequest());
        notify(app, "LOAN_SANCTIONED", java.util.Map.of(
                "applicantName", nullSafe(app.getApplicantName()),
                "applicationNumber", nullSafe(app.getApplicationNumber()),
                "approvedAmount", nullSafe(app.getApprovedAmount())
        ));
    }

    private void onEnterInitiateDisbursement(LoanApplication app) {
        documentGenerationService.generate(app.getId(), "WELCOME_LETTER", new GenerateDocumentRequest());
        notify(app, "DISBURSEMENT_INITIATED", java.util.Map.of(
                "applicantName", nullSafe(app.getApplicantName()),
                "applicationNumber", nullSafe(app.getApplicationNumber())
        ));
    }

    private void onEnterDisbursed(LoanApplication app) {
        notify(app, "LOAN_DISBURSED", java.util.Map.of(
                "applicantName", nullSafe(app.getApplicantName()),
                "loanAccountNumber", nullSafe(app.getLoanAccountNumber())
        ));
    }

    private void onEnterRejected(LoanApplication app, String remarks) {
        GenerateDocumentRequest request = new GenerateDocumentRequest();
        request.setExtraData(java.util.Map.of("rejectionReason", remarks != null ? remarks : ""));
        documentGenerationService.generate(app.getId(), "REJECTION_LETTER", request);
        notify(app, "LOAN_REJECTED", java.util.Map.of(
                "applicantName", nullSafe(app.getApplicantName()),
                "applicationNumber", nullSafe(app.getApplicationNumber()),
                "rejectionReason", remarks != null ? remarks : ""
        ));
    }

    private void notify(LoanApplication app, String templateCode, java.util.Map<String, Object> metadata) {
        if (app.getEmail() == null) {
            return;
        }
        NotificationRequest request = new NotificationRequest(
                templateCode,
                List.of(app.getEmail()),
                metadata
        );
        notificationClient.send(request);
    }

    private String nullSafe(Object value) {
        return value != null ? String.valueOf(value) : "";
    }
}
