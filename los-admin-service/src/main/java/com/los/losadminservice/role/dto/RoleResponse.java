package com.los.losadminservice.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private String roleId;

    private String roleName;

    private String departmentCode;

    private Boolean isTopLevel;

    private Boolean singleBranchOnly;

    private Boolean requiresManagerBranchAlign;

    private Integer maxPerBranch;

    private Integer maxDirectReports;

    private Boolean active;
}
