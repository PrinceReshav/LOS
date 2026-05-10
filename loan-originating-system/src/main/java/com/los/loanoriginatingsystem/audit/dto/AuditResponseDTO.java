package com.los.loanoriginatingsystem.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditResponseDTO {

    private String action;
    private String performedBy;
    private String role;

    private String oldStatus;
    private String newStatus;

    private String remarks;

    private LocalDateTime createdAt;
}