package com.los.losadminservice.hierarchyrule.dto;

import com.los.losadminservice.common.enums.BranchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyRuleResponse {

    private Long id;
    private String departmentCode;
    private String fromRoleId;
    private String toRoleId;
    private BranchType branchType;
    private Integer priority;
    private Boolean active;
}
