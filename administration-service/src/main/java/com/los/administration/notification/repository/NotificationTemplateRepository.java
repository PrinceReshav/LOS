package com.los.administration.notification.repository;

import com.los.administration.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplate, String> {

    Optional<NotificationTemplate> findByCodeAndActiveTrue(String code);
}