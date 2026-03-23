package com.los.administration.notification.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class IdempotencyRecord {

    @Id
    private String eventId;

    private LocalDateTime processedAt;
}