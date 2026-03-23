package com.los.administration.notification.scheduler;

import com.los.administration.notification.entity.NotificationLog;
import com.los.administration.notification.repository.NotificationLogRepository;
import com.los.administration.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationLogRepository logRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 60000) // every 1 min
    public void retryFailedNotifications() {

        List<NotificationLog> logs =
                logRepository.findByStatusAndNextRetryAtBefore(
                        "FAILED",
                        LocalDateTime.now()
                );

        if (logs.isEmpty()) {
            return;
        }

        log.info("Retrying {} notifications", logs.size());

        for (NotificationLog log : logs) {
            notificationService.trySend(log);
        }
    }
}