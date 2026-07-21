package com.los.administration.role.dto;

import com.los.administration.role.model.RoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank
    private String roleId;

    @NotBlank
    private String roleName;

    private RoleType roleType;

    private String description;

    /** roleId of the parent role, or null for a top-level role. */
    private String parentRoleId;

    private Boolean active;
}
