package com.los.loanoriginatingsystem.integration.config.dto;

import com.los.loanoriginatingsystem.integration.config.entity.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read view of an IntegrationConfig. Secrets are NEVER returned in full -
 * only a masked hint (last 4 chars) so an admin can confirm which key is
 * configured without the plaintext ever leaving the server after creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConfigResponse {

    private String id;
    private String clientName;
    private String displayName;
    private String description;
    private String baseUrl;
    private String httpMethod;
    private String contentType;
    private Integer timeoutMs;
    private AuthType authType;
    private String authHeaderName;
    private String apiKeyMasked;
    private String apiSecretMasked;
    private String staticHeaders;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
