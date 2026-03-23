package com.los.loanoriginatingsystem.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Data
public class OutboxEvent {

    @Id
    private String id;

    private String aggregateType;   // DEVIATION / LOAN / DOCUMENT
    private String aggregateId;

    private String eventType;       // NOTIFICATION

    @Column(columnDefinition = "TEXT")
    private String payload;         // JSON

    private String status;          // NEW / SENT / FAILED

    private Integer retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}