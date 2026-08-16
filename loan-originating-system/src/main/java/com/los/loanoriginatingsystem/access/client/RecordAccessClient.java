package com.los.loanoriginatingsystem.access.client;

import com.los.loanoriginatingsystem.access.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client into administration-service's record-level authorization
 * (RecordAccessService) - used to gate single-record reads/writes and to
 * fetch a user's visibility scope for filtering list queries.
 *
 * Fails CLOSED on a downstream error for single-record checks (deny by
 * default - an authorization service being unreachable must never be
 * treated as "allow"), but fails to an empty scope (own records only) for
 * the scope lookup, which has the same net effect: nothing extra is
 * revealed if administration-service is unreachable.
 */
@Component
@Slf4j
public class RecordAccessClient {

    private final RestTemplate restTemplate;
    private final String administrationServiceUrl;

    public RecordAccessClient(
            RestTemplate restTemplate,
            @Value("${administration.service.url}") String administrationServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.administrationServiceUrl = administrationServiceUrl;
    }

    public boolean canAccess(String requestingUserId, String recordType, String recordId,
                              String ownerUserId, String branchId, RecordAccessLevel requiredAccess) {
        try {
            RecordAccessCheckRequest request = new RecordAccessCheckRequest(
                    requestingUserId, recordType, recordId, ownerUserId, branchId, requiredAccess);

            RecordAccessCheckResponse response = restTemplate.postForObject(
                    administrationServiceUrl + "/internal/record-access/check",
                    request,
                    RecordAccessCheckResponse.class
            );

            return response != null && response.isAllowed();

        } catch (RestClientException e) {
            log.error("Record access check failed (fail-closed - denying access) for user={}, recordType={}, recordId={}: {}",
                    requestingUserId, recordType, recordId, e.getMessage());
            return false;
        }
    }

    public RecordAccessScopeResponse getScope(String requestingUserId, String recordType) {
        try {
            RecordAccessScopeRequest request = new RecordAccessScopeRequest(requestingUserId, recordType);

            RecordAccessScopeResponse response = restTemplate.postForObject(
                    administrationServiceUrl + "/internal/record-access/scope",
                    request,
                    RecordAccessScopeResponse.class
            );

            return response != null ? response : ownOnlyScope(requestingUserId);

        } catch (RestClientException e) {
            log.error("Record access scope lookup failed (falling back to own records only) for user={}: {}",
                    requestingUserId, e.getMessage());
            return ownOnlyScope(requestingUserId);
        }
    }

    private RecordAccessScopeResponse ownOnlyScope(String requestingUserId) {
        RecordAccessScopeResponse scope = new RecordAccessScopeResponse();
        scope.setSeesAll(false);
        scope.setVisibleOwnerUserIds(java.util.List.of(requestingUserId));
        scope.setVisibleBranchIds(java.util.List.of());
        scope.setVisibleRecordIds(java.util.List.of());
        return scope;
    }
}
