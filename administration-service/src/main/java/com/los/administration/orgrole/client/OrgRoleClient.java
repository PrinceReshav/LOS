package com.los.administration.orgrole.client;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.orgrole.dto.OrgRoleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Resolves organizational/hierarchy roles (FIELD_OFFICER,
 * RELATIONSHIP_MANAGER, CEO, ...) from los-admin-service's role
 * master-data catalog.
 *
 * WHY THIS EXISTS: administration-service used to send its own
 * system-access role (role_admin/role_sales) to los-admin-service when
 * creating an Employee. That role id doesn't exist in los-admin-service's
 * own Role table, so later hierarchy/branch validation there
 * (EmployeeHierarchyValidator) failed with "Role not found: role_sales".
 *
 * Fix: administration-service must resolve the REAL organizational role
 * from los-admin-service itself before creating the employee, so the
 * roleId that ends up on the Employee record is guaranteed to exist in
 * the catalog that later validates it.
 */
@Slf4j
@Service
public class OrgRoleClient {

    private final RestTemplate restTemplate;
    private final String losAdminServiceBaseUrl;

    public OrgRoleClient(
            RestTemplate restTemplate,
            @Value("${los-admin-service.base-url:http://localhost:8081}") String losAdminServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.losAdminServiceBaseUrl = losAdminServiceBaseUrl;
    }

    /**
     * Fetches and validates an organizational role by id.
     * Throws ResourceNotFoundException if it doesn't exist, is inactive,
     * or if los-admin-service can't be reached - callers should surface
     * this as a clean 4xx rather than let it fail later, silently, deep
     * inside a gRPC hierarchy check.
     */
    public OrgRoleResponse getActiveOrgRole(String orgRoleId) {

        if (orgRoleId == null || orgRoleId.isBlank()) {
            throw new ResourceNotFoundException("Org role not found: (blank)");
        }

        String url = losAdminServiceBaseUrl + "/admin/roles/" + orgRoleId.trim().toUpperCase();

        OrgRoleResponse role;

        try {
            role = restTemplate.getForObject(url, OrgRoleResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Org role not found: " + orgRoleId);
        } catch (ResourceAccessException ex) {
            log.error("Could not reach los-admin-service to resolve org role {}", orgRoleId, ex);
            throw new ResourceNotFoundException(
                    "Could not verify org role '" + orgRoleId + "' - los-admin-service is unreachable"
            );
        }

        if (role == null) {
            throw new ResourceNotFoundException("Org role not found: " + orgRoleId);
        }

        if (Boolean.FALSE.equals(role.getActive())) {
            throw new ResourceNotFoundException("Org role is inactive: " + orgRoleId);
        }

        return role;
    }
}