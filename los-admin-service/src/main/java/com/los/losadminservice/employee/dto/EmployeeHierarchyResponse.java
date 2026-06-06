package com.los.losadminservice.employee.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeHierarchyResponse {

    private String employeeId;

    private String fullName;

    private String roleName;

    private String managerEmployeeId;

    private List<EmployeeHierarchyResponse> children;
}