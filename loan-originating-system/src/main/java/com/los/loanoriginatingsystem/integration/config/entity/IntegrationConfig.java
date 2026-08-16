package com.los.loanoriginatingsystem.integration.config.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DB-backed, admin-editable configuration for every outbound integration
 * (CIBIL, AML, Aadhaar OCR/Verify, PAN, GST, e-Sign, e-NACH, Account
 * Aggregator, LMS, SurePass, MNRL, etc).
 *
 * This replaces/augments the old properties-file-only {@code http.clients.*}
 * config (see HttpClientConfig) so integrations can be added, rotated, or
 * disabled at runtime without a redeploy - equivalent to Salesforce's
 * HTTPCalloutConfiguration__mdt, but stored properly instead of as
 * deployable metadata.
 *
 * Secrets (apiKey / apiSecret) are stored encrypted at rest (see
 * EncryptionUtil) and are never returned as plain text by the read APIs -
 * see IntegrationConfigResponse.
 */
@Entity
@Table(name = "integration_config")
@Getter
@Setter
public class IntegrationConfig {

    @Id
    private String id;

    /** Logical client name used by HttpCalloutService.execute(clientName, ...). */
    @Column(name = "client_name", nullable = false, unique = true)
    private String clientName;

    /** Human readable label, e.g. "CIBIL Credit Bureau", "Aadhaar OCR (SurePass)". */
    private String displayName;

    private String description;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "http_method")
    private String httpMethod = "POST";

    @Column(name = "content_type")
    private String contentType = "application/json";

    @Column(name = "timeout_ms")
    private Integer timeoutMs = 10000;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false)
    private AuthType authType = AuthType.NONE;

    /** Header name to use for API_KEY_HEADER, e.g. "x-api-key", "X-Auth-Token". */
    @Column(name = "auth_header_name")
    private String authHeaderName;

    /** Encrypted at rest. Never exposed via GET responses - see service layer. */
    @Column(name = "api_key_encrypted", length = 2000)
    private String apiKeyEncrypted;

    /** Encrypted at rest. Used for BASIC_AUTH password, or a secondary secret. */
    @Column(name = "api_secret_encrypted", length = 2000)
    private String apiSecretEncrypted;

    /** Extra static headers, stored as "Header-Name:value;Header-Name2:value2". */
    @Column(name = "static_headers", length = 2000)
    private String staticHeaders;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (active == null) active = true;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
