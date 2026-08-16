package com.los.events;

import java.io.Serializable;

public record UserCreatedEvent(

        String userId,
        String employeeId,
        String username,
        String email,
        String mobile,
        String firstName,
        String lastName,

        // administration-service's system-access role (ADMIN/SALES) -
        // login/RBAC only, NOT a valid id in los-admin-service's role
        // catalog. Kept for completeness/audit but must not be used to
        // populate Employee.roleId in los-admin-service.
        String roleId,
        String roleName,

        // FIX: the organizational/hierarchy role (e.g. FIELD_OFFICER,
        // RELATIONSHIP_MANAGER, CEO) resolved from los-admin-service's
        // own role catalog. This - not roleId/roleName above - is what
        // must be used when creating/updating the Employee record on
        // the los-admin-service side (via gRPC AND via this Kafka
        // fallback path), since that's the only catalog
        // EmployeeHierarchyValidator checks against.
        String orgRoleId,
        String orgRoleName,

        String profileId,
        String profileName
) implements Serializable {}