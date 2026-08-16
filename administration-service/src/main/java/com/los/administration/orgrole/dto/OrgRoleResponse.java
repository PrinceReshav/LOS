package com.los.administration.orgrole.dto;

import lombok.Data;

/**
 * Mirrors los-admin-service's RoleResponse (GET /admin/roles/{roleId}).
 *
 * This is intentionally a thin, decoupled copy rather than a shared
 * module - administration-service only needs enough of the org role
 * to validate it exists/is active and to display its name. The
 * source of truth for this data always remains los-admin-service.
 */
@Data
public class OrgRoleResponse {

    private String roleId;
    private String roleName;
    private String departmentCode;
    private Boolean isTopLevel;
    private Boolean active;
}