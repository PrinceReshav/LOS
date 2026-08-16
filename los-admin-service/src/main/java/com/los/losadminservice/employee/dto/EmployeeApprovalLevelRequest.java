package com.los.losadminservice.employee.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeApprovalLevelRequest {

    @NotNull
    @Min(0) @Max(5)
    private Integer approvalLevel;

    @NotBlank
    private String approverRoleCode;
}
