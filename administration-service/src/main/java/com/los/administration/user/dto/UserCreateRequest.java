package com.los.administration.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String mobile;

    @NotBlank
    private String alias;

    @NotBlank
    private String firstName;

    private String middleName; // optional

    @NotBlank
    private String lastName;

    @NotBlank
    private String employeeId;

    // FIX: the User entity's role column is NOT NULL and every other
    // module treats role as mandatory, but this field previously had no
    // validation. A missing roleName used to flow through as a bare
    // null into roleRepository.findByRoleName(null) instead of failing
    // request validation with a clean 400.
    // This is administration-service's own system-access role
    // (ADMIN/SALES) - used for login/RBAC only.
    @NotBlank
    private String roleName;

    // FIX: this was missing entirely, so employee creation had no way
    // to capture the actual ORGANIZATIONAL role (FIELD_OFFICER,
    // RELATIONSHIP_MANAGER, CEO, ...) that los-admin-service needs for
    // branch/reporting-manager hierarchy validation. Without it,
    // createUser() had no choice but to forward the system-access role
    // instead, which doesn't exist in los-admin-service's role catalog
    // and caused "Role not found: role_sales" / "role_admin" later.
    //
    // Must be a valid roleId from los-admin-service's /admin/roles
    // catalog (e.g. FIELD_OFFICER, RELATIONSHIP_MANAGER, CEO).
    @NotBlank
    private String orgRoleId;

    @NotBlank
    private String profileName;

    // FIX: UserCreationService.createUser does
    // UserLicenseType.valueOf(req.getLicenseType()) unconditionally, so a
    // missing licenseType previously threw an unhandled NPE deep inside
    // user creation (and made every bulk-uploaded row fail, since the
    // Excel parser never even read this column). Validate it up front.
    @NotBlank
    private String licenseType;
}