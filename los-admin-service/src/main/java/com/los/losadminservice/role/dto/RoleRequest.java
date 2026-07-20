package com.los.losadminservice.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank
    private String roleId;

    @NotBlank
    private String roleName;

    @NotBlank
    private String departmentCode;

    private Boolean isTopLevel;

    private Boolean singleBranchOnly;

    private Boolean requiresManagerBranchAlign;

    private Integer maxPerBranch;

    private Integer maxDirectReports;

    private Boolean active;
}
