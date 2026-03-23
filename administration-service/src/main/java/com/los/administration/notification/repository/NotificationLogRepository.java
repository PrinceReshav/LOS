package com.los.administration.notification.repository;

import com.los.administration.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {

    List<NotificationLog> findByStatusAndRetryCountLessThan(String status, Integer retryCount);

    List<NotificationLog> findByStatusAndNextRetryAtBefore(
            String status,
            LocalDateTime time
    );
}