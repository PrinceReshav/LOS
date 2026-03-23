package com.los.administration.notification.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_log")
@Data
public class NotificationLog {

    @Id
    private String id;

    private String templateCode;
    private String type;

    @Column(length = 2000)
    private String recipients;

    private String subject;

    @Column(length = 5000)
    private String body;

    private String status; // PENDING / SENT / FAILED

    private Integer retryCount;

    private LocalDateTime lastAttemptAt;
    private LocalDateTime nextRetryAt;

    private LocalDateTime createdAt;
}