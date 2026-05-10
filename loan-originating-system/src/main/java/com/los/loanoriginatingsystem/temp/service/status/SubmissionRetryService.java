package com.los.loanoriginatingsystem.temp.service.status;

import com.los.loanoriginatingsystem.outbox.service.OutboxService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionRetryService {

    private final TempLoanApplicationRepository tempRepo;
    private final OutboxService outboxService;

    private static final int MAX_RETRY = 3;

    @Transactional
    public String retry(String submissionRef) {

        // =============================
        // 🔍 FETCH
        // =============================
        TempLoanApplication temp =
                tempRepo.findBySubmissionRef(submissionRef)
                        .orElseThrow(() -> new RuntimeException("Invalid submissionRef"));

        // =============================
        // ❌ ONLY FAILED CAN RETRY
        // =============================
        if (!"FAILED".equals(temp.getSubmissionStatus())) {
            throw new RuntimeException("Only FAILED applications can be retried");
        }

        // =============================
        // 🔒 RETRY LIMIT
        // =============================
        if (temp.getRetryCount() != null && temp.getRetryCount() >= MAX_RETRY) {
            throw new RuntimeException("Retry limit exceeded");
        }

        // =============================
        // 🔄 RESET STATE
        // =============================
        temp.setSubmissionStatus("INITIATED");
        temp.setFailureReason(null);

        temp.setRetryCount(
                temp.getRetryCount() == null ? 1 : temp.getRetryCount() + 1
        );

        temp.setLastRetryAt(LocalDateTime.now().toString());

        tempRepo.save(temp);

        // =============================
        // 📦 RE-TRIGGER OUTBOX
        // =============================
        outboxService.saveEvent(
                "LOAN",
                temp.getId(),
                "LOAN_SUBMITTED",
                temp
        );

        return "Retry triggered successfully";
    }
}