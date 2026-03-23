package com.los.administration.notification.repository;

import com.los.administration.notification.entity.NotificationDLQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDLQRepository
        extends JpaRepository<NotificationDLQ, String> {
}