package com.los.loanoriginatingsystem.outbox.scheduler;

import com.los.loanoriginatingsystem.audit.service.AuditService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.outbox.service.OutboxService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final TempLoanApplicationRepository tempRepo;
    private final OutboxService outboxService;
    private final AuditService auditService;

    private static final int MAX_RETRY = 3;

    @Scheduled(fixedDelay = 30000)
    public void retryFailed() {

        List<TempLoanApplication> failed =
                tempRepo.findBySubmissionStatus("FAILED");

        for (TempLoanApplication temp : failed) {

            // =============================
            // 🔒 MAX RETRY → DLQ
            // =============================
            if (temp.getRetryCount() != null &&
                    temp.getRetryCount() >= MAX_RETRY) {

                String oldStatus = temp.getSubmissionStatus();

                temp.setSubmissionStatus("FAILED_FINAL");
                tempRepo.save(temp);

                auditService.log(
                        "TEMP",
                        temp.getId(),
                        "AUTO_FAIL",
                        oldStatus,
                        "FAILED_FINAL",
                        "Retry limit exceeded"
                );

                continue;
            }

            // =============================
            // ⏳ COOLDOWN
            // =============================
            if (temp.getLastRetryAt() != null) {

                long seconds = Duration.between(
                        temp.getLastRetryAt(),
                        LocalDateTime.now()
                ).getSeconds();

                if (seconds < 60) continue;
            }

            // =============================
            // 🔄 RETRY (ONLY ONCE)
            // =============================
            String oldStatus = temp.getSubmissionStatus();

            temp.setSubmissionStatus("INITIATED");

            temp.setRetryCount(
                    temp.getRetryCount() == null
                            ? 1
                            : temp.getRetryCount() + 1
            );

            temp.setLastRetryAt(LocalDateTime.now());

            tempRepo.save(temp);

            auditService.log(
                    "TEMP",
                    temp.getId(),
                    "AUTO_RETRY",
                    oldStatus,
                    "INITIATED",
                    "System retry"
            );

            // =============================
            // 📦 OUTBOX
            // =============================
            outboxService.saveEvent(
                    "LOAN",
                    temp.getId(),
                    "LOAN_SUBMITTED",
                    temp
            );
        }
    }
}