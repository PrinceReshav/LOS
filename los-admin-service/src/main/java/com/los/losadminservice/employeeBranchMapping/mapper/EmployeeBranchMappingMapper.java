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

        return new EmployeeBranchMappingResponse(
                mapping.getId(),
                mapping.getEmployeeId(),
                employee != null ? employee.getFullName() : null,
                mapping.getBranchId(),
                branch != null ? branch.getBranchName() : null,
                mapping.getPrimaryBranch(),
                mapping.getActive(),
                mapping.getAssignedAt(),
                mapping.getRelievedAt()
        );
    }
}
