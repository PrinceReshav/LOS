package com.los.losadminservice.employeeBranchMapping.validator;

import com.los.losadminservice.branch.repository.BranchRepository;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeBranchMappingValidator {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final EmployeeBranchMappingRepository repository;

    public void validateCreate(
            String employeeId,
            String branchId
    ) {

        if (!employeeRepository.existsByEmployeeId(employeeId)) {
            throw new RuntimeException("Employee not found");
        }

        if (!branchRepository.existsById(branchId)) {
            throw new RuntimeException("Branch not found");
        }

        if (repository.existsByEmployeeIdAndBranchId(
                employeeId,
                branchId
        )) {

            throw new RuntimeException(
                    "Employee already mapped to branch"
            );
        }
    }
}