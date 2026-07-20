package com.los.losadminservice.employee.validator;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import com.los.losadminservice.role.model.Role;
import com.los.losadminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Enforces the per-branch headcount cap for a role, e.g. "a branch can have
 * only 2 Relationship Managers". The cap itself is configured on the Role
 * (Role.maxPerBranch) - nothing here is hardcoded to Sales/Credit/any
 * specific role, so the same engine protects every department.
 */
@Component
@RequiredArgsConstructor
public class BranchCapacityValidator {

    private final EmployeeBranchMappingRepository mappingRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleService roleService;

    public void validateBranchCapacity(String branchId, String roleId) {

        Role role = roleService.getEntity(roleId);

        if (role.getMaxPerBranch() == null) {
            // unlimited for this role
            return;
        }

        List<EmployeeBranchMapping> mappings =
                mappingRepository.findByBranchIdAndActiveTrue(branchId);

        long currentCount = mappings.stream()
                .map(mapping ->
                        employeeRepository.findByEmployeeId(mapping.getEmployeeId()).orElse(null)
                )
                .filter(java.util.Objects::nonNull)
                .filter(e -> roleId.equalsIgnoreCase(e.getRoleId()))
                .count();

        if (currentCount >= role.getMaxPerBranch()) {
            throw new BusinessRuleViolationException(
                    "Branch already has the maximum of " + role.getMaxPerBranch() +
                            " " + role.getRoleName() + "(s)"
            );
        }
    }
}
