package com.los.losadminservice.hierarchyrule.dto;

import com.los.losadminservice.common.enums.BranchType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HierarchyRuleRequest {

    @NotBlank
    private String departmentCode;

    @NotBlank
    private String fromRoleId;

    @NotBlank
    private String toRoleId;

    /** null = applies to any branch type */
    private BranchType branchType;

    private Integer priority;

    private Boolean active;
}
