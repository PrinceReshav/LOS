package com.los.administration.notification.alert;

import com.los.administration.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final EmailService emailService;
    private final AlertProperties properties;
    private final SlackAlertService slackService;

    public void notifyFailure(String logId, String reason) {

        String message = buildMessage(logId, reason);

        // 🔥 1. ALWAYS LOG
        log.error(message);

        // 🔥 2. EMAIL ALERT
        if (properties.getEmail().isEnabled()) {

            List<String> recipients = properties.getEmail().getRecipientsList();

            emailService.send(
                    recipients,
                    "🚨 ALERT: Notification Failure",
                    message
            );
        }

        // 🔥 3. SLACK ALERT
        slackService.send(message);
    }

    private String buildMessage(String logId, String reason) {

        return """
                🚨 NOTIFICATION FAILURE

                Log ID: %s
                Reason: %s
                Time: %s
                """.formatted(
                logId,
                reason,
                java.time.LocalDateTime.now()
        );
    }
}