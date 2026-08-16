package com.los.administration.user.dto;

import com.los.administration.license.model.UserLicenseType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String userId;

    private String username;
    private String email;
    private String mobile;

    private String alias;
    private String firstName;
    private String middleName;
    private String lastName;

    private String employeeId;

    // @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    // The organizational/hierarchy role (e.g. FIELD_OFFICER) resolved
    // from los-admin-service - distinct from roleName above, which is
    // administration-service's own system-access role (ADMIN/SALES).
    private String orgRoleId;
    private String orgRoleName;

    private String profileName;

    private UserLicenseType licenseType;


    private boolean active;
}