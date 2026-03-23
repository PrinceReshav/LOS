package com.los.administration.audit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogEntry {

    private String userId;
    private String role;

    private String httpMethod;
    private String endpoint;
    private String action;

    private int httpStatus;
    private boolean success;

    private LocalDateTime timestamp;
}
