package com.los.losadminservice.employeeBranchMapping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeBranchMappingCreateRequest {

    @NotBlank
    private String employeeId;

    @NotBlank
    private String branchId;

    /**
     * If true, this mapping becomes the employee's primary branch (and any
     * other active mapping is demoted). If omitted, the very first mapping
     * an employee gets is automatically made primary.
     */
    private Boolean primaryBranch;
}
