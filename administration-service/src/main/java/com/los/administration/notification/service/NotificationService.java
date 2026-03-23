package com.los.administration.notification.service;

import com.los.administration.notification.alert.AlertService;
import com.los.administration.notification.dto.NotificationRequest;
import com.los.administration.email.service.EmailService;
import com.los.administration.notification.engine.TemplateEngine;
import com.los.administration.notification.entity.NotificationDLQ;
import com.los.administration.notification.entity.NotificationLog;
import com.los.administration.notification.entity.NotificationTemplate;
import com.los.administration.notification.repository.NotificationDLQRepository;
import com.los.administration.notification.repository.NotificationLogRepository;
import com.los.administration.notification.repository.NotificationTemplateRepository;
import com.los.administration.notification.retry.RetryStrategy;
import com.los.administration.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationTemplateRepository templateRepo;
    private final NotificationDLQRepository  dlqRepository;
    private final TemplateEngine templateEngine;
    private final NotificationLogRepository logRepository;
    private final RetryStrategy retryStrategy;
    private final AlertService  alertService;


    /**
     * 🔥 ENTRY POINT
     */
    public void send(NotificationRequest req) {

        NotificationTemplate template =
                templateRepo.findByCodeAndActiveTrue(req.getTemplateCode())
                        .orElseThrow(() ->
                                new RuntimeException("Template not found: " + req.getTemplateCode())
                        );

        // 🔥 PROCESS TEMPLATE
        String subject = templateEngine.process(
                template.getSubject(),
                req.getMetadata()
        );

        String body = templateEngine.process(
                template.getBody(),
                req.getMetadata()
        );

        // 🔥 SAVE LOG
        NotificationLog log = new NotificationLog();

        log.setId(UUID.randomUUID().toString());
        log.setTemplateCode(req.getTemplateCode());
        log.setType(template.getType());
        log.setRecipients(String.join(",", req.getRecipients())); // ✅ FIX
        log.setSubject(subject);
        log.setBody(body);
        log.setStatus("PENDING");
        log.setRetryCount(0);
        log.setCreatedAt(LocalDateTime.now());

        logRepository.save(log);

        // 🔥 TRY SEND
        trySend(log);
    }

    /**
     * 🔁 RETRYABLE METHOD
     */
    public void trySend(NotificationLog log) {

        try {

            List<String> recipients =
                    List.of(log.getRecipients().split(","));

            if ("EMAIL".equalsIgnoreCase(log.getType())) {

                emailService.send(
                        recipients,
                        log.getSubject(),
                        log.getBody()
                );

            } else if ("SMS".equalsIgnoreCase(log.getType())) {

                smsService.send(
                        recipients,
                        log.getBody()
                );
            }

            log.setStatus("SENT");
            log.setNextRetryAt(null);

        } catch (Exception e) {

            int retry = log.getRetryCount() + 1;
            log.setRetryCount(retry);

            LocalDateTime nextRetry =
                    retryStrategy.nextRetryTime(retry - 1);

            if (nextRetry == null) {

                // 💀 MOVE TO DLQ
                log.setStatus("FAILED_PERMANENT");

                moveToDLQ(log, e.getMessage());

                // 🚨 ALERT
                alertService.notifyFailure(log.getId(), e.getMessage());

            } else {
                log.setStatus("FAILED");
                log.setNextRetryAt(nextRetry);
            }
        }

        log.setLastAttemptAt(LocalDateTime.now());

        logRepository.save(log);
    }
    private void moveToDLQ(NotificationLog log, String reason) {

        NotificationDLQ dlq = new NotificationDLQ();

        dlq.setId(UUID.randomUUID().toString());
        dlq.setOriginalLogId(log.getId());
        dlq.setTemplateCode(log.getTemplateCode());
        dlq.setType(log.getType());
        dlq.setRecipients(log.getRecipients());
        dlq.setSubject(log.getSubject());
        dlq.setBody(log.getBody());
        dlq.setFailureReason(reason);
        dlq.setCreatedAt(LocalDateTime.now());

        dlqRepository.save(dlq);
    }
}