package com.los.loanoriginatingsystem.integration.config.dto;

import com.los.loanoriginatingsystem.integration.config.entity.AuthType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IntegrationConfigRequest {

    @NotBlank
    private String clientName;

    private String displayName;
    private String description;

    @NotBlank
    private String baseUrl;

    private String httpMethod;
    private String contentType;
    private Integer timeoutMs;

    @NotNull
    private AuthType authType;

    private String authHeaderName;

    /**
     * Plain text on the way in only - encrypted before persisting.
     * Omit (null) on update to leave the currently-stored secret untouched.
     */
    private String apiKey;
    private String apiSecret;

    private String staticHeaders;
}
