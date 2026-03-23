package com.los.loanoriginatingsystem.integration.callback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "callback_request",
        uniqueConstraints = @UniqueConstraint(columnNames = "request_id")
)
@Data
public class CallbackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "received_at")
    private LocalDateTime receivedAt = LocalDateTime.now();
}