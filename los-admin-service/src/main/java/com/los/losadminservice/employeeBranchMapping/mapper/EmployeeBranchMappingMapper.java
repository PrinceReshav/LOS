package com.los.losadminservice.employeeBranchMapping.mapper;

import com.los.losadminservice.branch.model.Branch;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;

public final class EmployeeBranchMappingMapper {

    private EmployeeBranchMappingMapper() {
    }

    public static EmployeeBranchMappingResponse toResponse(
            EmployeeBranchMapping mapping,
            Employee employee,
            Branch branch
    ) {

        return EmployeeBranchMappingResponse.builder()
                .id(mapping.getId())
                .employeeId(mapping.getEmployeeId())
                .employeeName(
                        employee != null
                                ? employee.getFullName()
                                : null
                )
                .branchId(mapping.getBranchId())
                .branchName(
                        branch != null
                                ? branch.getBranchName()
                                : null
                )
                .active(mapping.getActive())
                .assignedAt(mapping.getAssignedAt())
                .relievedAt(mapping.getRelievedAt())
                .build();
    }
}