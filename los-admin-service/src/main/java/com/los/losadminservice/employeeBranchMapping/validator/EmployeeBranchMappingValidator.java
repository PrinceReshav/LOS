package com.los.losadminservice.employeeBranchMapping.validator;

import com.los.losadminservice.branch.repository.BranchRepository;
import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.validator.BranchManagerAlignmentValidator;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import com.los.losadminservice.role.model.Role;
import com.los.losadminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeBranchMappingValidator {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final EmployeeBranchMappingRepository repository;
    private final RoleService roleService;
    private final BranchManagerAlignmentValidator branchManagerAlignmentValidator;

    /**
     * @return the employee being mapped, so the service doesn't need to
     * re-fetch it.
     */
    public Employee validateCreate(String employeeId, String branchId) {

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found: " + branchId);
        }

        if (repository.existsByEmployeeIdAndBranchIdAndActiveTrue(employeeId, branchId)) {
            throw new BusinessRuleViolationException("Employee already actively mapped to this branch");
        }

        if (employee.getRoleId() == null) {
            throw new BusinessRuleViolationException(
                    "Employee must be assigned a Role before being mapped to a branch"
            );
        }

        Role role = roleService.getEntity(employee.getRoleId());

        List<com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping> existingMappings =
                repository.findByEmployeeIdAndActiveTrue(employeeId);

        if (Boolean.TRUE.equals(role.getSingleBranchOnly()) && !existingMappings.isEmpty()) {
            throw new BusinessRuleViolationException(
                    role.getRoleName() + " can only be mapped to one branch at a time"
            );
        }

        if (Boolean.TRUE.equals(role.getRequiresManagerBranchAlign())) {
            branchManagerAlignmentValidator.validateManagerCoversBranch(
                    employee.getManagerEmployeeId(),
                    branchId,
                    role.getRoleName()
            );
        }

        return employee;
    }
}
