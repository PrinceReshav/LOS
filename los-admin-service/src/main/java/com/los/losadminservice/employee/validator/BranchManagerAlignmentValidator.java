package com.los.losadminservice.employee.validator;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Enforces: "a Branch Credit Manager (or any role flagged
 * requiresManagerBranchAlign) can be mapped to multiple branches, provided
 * their reporting manager is also mapped to that branch."
 *
 * This keeps the rule generic (driven by Role.requiresManagerBranchAlign)
 * rather than hardcoded to the BCM role specifically, so any future
 * multi-branch role can opt into the same guarantee purely via master data.
 */
@Component
@RequiredArgsConstructor
public class BranchManagerAlignmentValidator {

    private final EmployeeBranchMappingRepository mappingRepository;

    /**
     * Called when mapping an employee to a new branch: if the employee
     * already has a manager, that manager must already be actively mapped
     * to the same branch.
     */
    public void validateManagerCoversBranch(String managerEmployeeId, String branchId, String roleLabel) {

        if (managerEmployeeId == null) {
            // "if not [present] it can be empty" - no manager yet is allowed,
            // the branch mapping can be created and reconciled later.
            return;
        }

        boolean managerMappedToBranch =
                mappingRepository.existsByEmployeeIdAndBranchIdAndActiveTrue(managerEmployeeId, branchId);

        if (!managerMappedToBranch) {
            throw new BusinessRuleViolationException(
                    roleLabel + " can only be mapped to a branch where their reporting manager is also mapped. " +
                            "Map the manager to this branch first, or map to a branch the manager already covers."
            );
        }
    }

    /**
     * Called when assigning/changing an employee's manager: the new manager
     * must already cover every branch the employee is currently actively
     * mapped to.
     */
    public void validateNewManagerCoversAllCurrentBranches(
            String employeeId,
            String newManagerEmployeeId,
            String roleLabel
    ) {

        List<String> employeeBranchIds = mappingRepository
                .findByEmployeeIdAndActiveTrue(employeeId)
                .stream()
                .map(m -> m.getBranchId())
                .toList();

        for (String branchId : employeeBranchIds) {

            boolean managerMappedToBranch =
                    mappingRepository.existsByEmployeeIdAndBranchIdAndActiveTrue(newManagerEmployeeId, branchId);

            if (!managerMappedToBranch) {
                throw new BusinessRuleViolationException(
                        "Cannot assign this manager: " + roleLabel +
                                " is currently mapped to branch " + branchId +
                                " which the new manager does not cover."
                );
            }
        }
    }
}
