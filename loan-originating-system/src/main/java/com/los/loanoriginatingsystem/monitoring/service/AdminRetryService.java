package com.los.loanoriginatingsystem.monitoring.service;

import com.los.loanoriginatingsystem.audit.service.AuditService;
import com.los.loanoriginatingsystem.outbox.service.OutboxService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRetryService {

    private final TempLoanApplicationRepository repo;
    private final OutboxService outboxService;
    private final AuditService auditService;

    @Transactional
    public String retryFromDLQ(String tempId) {

        TempLoanApplication temp =
                repo.findById(tempId)
                        .orElseThrow(() -> new RuntimeException("Temp not found"));

        // =============================
        // ONLY FAILED_FINAL
        // =============================
        if (!"FAILED_FINAL".equals(temp.getSubmissionStatus())) {
            throw new RuntimeException("Only FAILED_FINAL can be retried by admin");
        }

        // 🔥 CAPTURE OLD STATE
        String oldStatus = temp.getSubmissionStatus();


        // 🔥 ✅ AUDIT HERE (AFTER SAVE, USING OLD STATE)
        auditService.log(
                "TEMP",
                tempId,
                "RETRY",
                oldStatus,
                "INITIATED",
                "Manual retry triggered"
        );

        // =============================
        // RESET STATE
        // =============================
        temp.setSubmissionStatus("INITIATED");
        temp.setFailureReason(null);

        repo.save(temp);

        // =============================
        // RE-TRIGGER
        // =============================
        outboxService.saveEvent(
                "LOAN",
                temp.getId(),
                "LOAN_SUBMITTED",
                temp
        );

        return "Admin retry triggered";
    }



/*
* 👉 This is used when:

Loan already processed outside system
KYC manually verified
System stuck but business wants to proceed
*
*/
@Transactional
public String forceComplete(String tempId, String loanId) {

    TempLoanApplication temp =
            repo.findById(tempId)
                    .orElseThrow(() -> new RuntimeException("Temp not found"));

    String oldStatus = temp.getSubmissionStatus();

    temp.setSubmissionStatus("COMPLETED");

    repo.save(temp);

    // 🔥 AUDIT
    auditService.log(
            "TEMP",
            tempId,
            "FORCE_SUCCESS",
            oldStatus,
            "COMPLETED",
            "Admin override with loanId=" + loanId
    );

    return "Manually marked as COMPLETED with loanId=" + loanId;
}
    @Transactional
    public String forceFail(String tempId, String reason) {

        TempLoanApplication temp =
                repo.findById(tempId)
                        .orElseThrow(() -> new RuntimeException("Temp not found"));

        String oldStatus = temp.getSubmissionStatus();

        temp.setSubmissionStatus("FAILED_FINAL");
        temp.setFailureReason(reason);

        repo.save(temp);

        // 🔥 AUDIT
        auditService.log(
                "TEMP",
                tempId,
                "FORCE_FAIL",
                oldStatus,
                "FAILED_FINAL",
                reason
        );

        return "Marked as FAILED_FINAL";
    }


}