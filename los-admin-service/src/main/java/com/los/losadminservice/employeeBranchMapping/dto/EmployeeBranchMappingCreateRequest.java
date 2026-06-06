package com.los.losadminservice.employeeBranchMapping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeBranchMappingCreateRequest {

    @NotBlank
    private String employeeId;

    @NotBlank
    private String branchId;

}