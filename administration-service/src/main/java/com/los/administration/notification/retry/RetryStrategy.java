package com.los.administration.notification.retry;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RetryStrategy {

    public LocalDateTime nextRetryTime(int retryCount) {

        return switch (retryCount) {

            case 0 -> LocalDateTime.now().plusMinutes(1);
            case 1 -> LocalDateTime.now().plusMinutes(5);
            case 2 -> LocalDateTime.now().plusMinutes(15);

            default -> null; // ❌ STOP RETRY
        };
    }
}