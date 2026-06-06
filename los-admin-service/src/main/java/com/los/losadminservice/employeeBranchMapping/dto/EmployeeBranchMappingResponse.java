package com.los.losadminservice.employeeBranchMapping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeBranchMappingResponse {

    private Long id;

    private String employeeId;
    private String employeeName;

    private String branchId;
    private String branchName;

    private Boolean active;

    private LocalDateTime assignedAt;

    private LocalDateTime relievedAt;

    public EmployeeBranchMappingResponse(
            Long id,
            String employeeId,
            String employeeName,
            String branchId,
            String branchName,
            Boolean active,
            LocalDateTime assignedAt,
            LocalDateTime relievedAt
    ) {

        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.branchId = branchId;
        this.branchName = branchName;
        this.active = active;
        this.assignedAt = assignedAt;
        this.relievedAt = relievedAt;
    }
}