package com.los.loanoriginatingsystem.integration.config.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.common.security.EncryptionUtil;
import com.los.loanoriginatingsystem.integration.config.dto.IntegrationConfigRequest;
import com.los.loanoriginatingsystem.integration.config.dto.IntegrationConfigResponse;
import com.los.loanoriginatingsystem.integration.config.entity.IntegrationConfig;
import com.los.loanoriginatingsystem.integration.config.repository.IntegrationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Admin CRUD + activation for outbound integration configs, and the
 * runtime lookup used by HttpCalloutService to resolve a client's base
 * URL, timeout, and (decrypted, in-memory-only) credentials.
 *
 * This is the Java equivalent of Salesforce's HTTPCalloutConfiguration__mdt
 * + the AllAPILogger pattern - except editable at runtime instead of being
 * deployable metadata, and with secrets actually encrypted at rest.
 */
@Service
@RequiredArgsConstructor
public class IntegrationConfigService {

    private final IntegrationConfigRepository repository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public IntegrationConfigResponse create(IntegrationConfigRequest request) {

        if (repository.existsByClientName(request.getClientName())) {
            throw new IllegalArgumentException(
                    "An integration config already exists for clientName: " + request.getClientName());
        }

        IntegrationConfig entity = new IntegrationConfig();
        entity.setId(UUID.randomUUID().toString());
        applyRequest(entity, request);

        return toResponse(repository.save(entity));
    }

    @Transactional
    public IntegrationConfigResponse update(String id, IntegrationConfigRequest request) {

        IntegrationConfig entity = getEntityOrThrow(id);
        applyRequest(entity, request);

        return toResponse(repository.save(entity));
    }

    @Transactional
    public IntegrationConfigResponse setActive(String id, boolean active) {

        IntegrationConfig entity = getEntityOrThrow(id);
        entity.setActive(active);

        return toResponse(repository.save(entity));
    }

    public IntegrationConfigResponse get(String id) {
        return toResponse(getEntityOrThrow(id));
    }

    public List<IntegrationConfigResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Runtime lookup for HttpCalloutService - returns the entity with
     * secrets still encrypted; the caller must use decrypt(...) explicitly
     * so plaintext credentials never sit in a DTO longer than one method
     * call's scope.
     */
    public IntegrationConfig getActiveForRuntime(String clientName) {
        return repository.findByClientNameAndActiveTrue(clientName).orElse(null);
    }

    public String decryptApiKey(IntegrationConfig config) {
        return encryptionUtil.decrypt(config.getApiKeyEncrypted());
    }

    public String decryptApiSecret(IntegrationConfig config) {
        return encryptionUtil.decrypt(config.getApiSecretEncrypted());
    }

    // ==========================================================

    private void applyRequest(IntegrationConfig entity, IntegrationConfigRequest request) {
        entity.setClientName(request.getClientName());
        entity.setDisplayName(request.getDisplayName());
        entity.setDescription(request.getDescription());
        entity.setBaseUrl(request.getBaseUrl());
        entity.setHttpMethod(request.getHttpMethod() != null ? request.getHttpMethod() : "POST");
        entity.setContentType(request.getContentType() != null ? request.getContentType() : "application/json");
        entity.setTimeoutMs(request.getTimeoutMs() != null ? request.getTimeoutMs() : 10000);
        entity.setAuthType(request.getAuthType());
        entity.setAuthHeaderName(request.getAuthHeaderName());
        entity.setStaticHeaders(request.getStaticHeaders());

        // Only overwrite secrets if the caller actually supplied a new value -
        // leaves the stored credential untouched on a plain metadata edit.
        if (request.getApiKey() != null && !request.getApiKey().isBlank()) {
            entity.setApiKeyEncrypted(encryptionUtil.encrypt(request.getApiKey()));
        }
        if (request.getApiSecret() != null && !request.getApiSecret().isBlank()) {
            entity.setApiSecretEncrypted(encryptionUtil.encrypt(request.getApiSecret()));
        }
    }

    private IntegrationConfig getEntityOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Integration config not found: " + id));
    }

    private IntegrationConfigResponse toResponse(IntegrationConfig e) {
        return new IntegrationConfigResponse(
                e.getId(),
                e.getClientName(),
                e.getDisplayName(),
                e.getDescription(),
                e.getBaseUrl(),
                e.getHttpMethod(),
                e.getContentType(),
                e.getTimeoutMs(),
                e.getAuthType(),
                e.getAuthHeaderName(),
                encryptionUtil.mask(safeDecrypt(e.getApiKeyEncrypted())),
                encryptionUtil.mask(safeDecrypt(e.getApiSecretEncrypted())),
                e.getStaticHeaders(),
                e.getActive(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getUpdatedBy()
        );
    }

    private String safeDecrypt(String encrypted) {
        if (encrypted == null) return null;
        try {
            return encryptionUtil.decrypt(encrypted);
        } catch (Exception e) {
            return null;
        }
    }
}
