package com.los.administration.notification.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_dlq")
@Data
public class NotificationDLQ {

    @Id
    private String id;

    private String originalLogId;
    private String templateCode;
    private String type;

    @Column(length = 2000)
    private String recipients;

    private String subject;

    @Column(length = 5000)
    private String body;

    @Column(length = 5000)
    private String failureReason;

    private LocalDateTime createdAt;
}