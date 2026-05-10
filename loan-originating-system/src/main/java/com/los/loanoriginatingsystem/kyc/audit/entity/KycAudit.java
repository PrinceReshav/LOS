package com.los.loanoriginatingsystem.kyc.audit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_audit")
@Data
public class KycAudit {

    @Id
    private String id;

    private String tempId;
    private String kycType;

    private String status; // SUCCESS / FAILED

    @Lob
    private String requestPayload;

    @Lob
    private String responsePayload;

    private String errorMessage;

    private LocalDateTime createdAt;
}