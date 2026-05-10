package com.los.loanoriginatingsystem.audit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_audit")
@Data
public class ActionAudit {

    @Id
    private String id;

    private String entityType;     // LOAN / TEMP / LEAD
    private String entityId;

    private String action;         // RETRY / FORCE_SUCCESS / FORCE_FAIL / SUBMIT

    private String performedBy;    // USERNAME from JWT
    private String role;           // ADMIN / OPS / FO

    private String oldStatus;
    private String newStatus;

    @Column(length = 5000)
    private String remarks;

    private String hash;
    private String previousHash;

    private LocalDateTime createdAt;
}