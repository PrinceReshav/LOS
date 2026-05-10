package com.los.administration.outbox.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "outbox_events",
        indexes = @Index(name = "idx_outbox_published", columnList = "published")
)
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "retry_count")
    private int retryCount;

    private boolean published = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}