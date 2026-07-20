package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Single entry point services call before persisting a manager assignment.
 * Runs, in order:
 *   1. Role / department / branch-context hierarchy validation
 *      (EmployeeHierarchyValidator - also covers top-level roles, circular
 *      reporting, and BCM-style branch alignment).
 *   2. Manager capacity validation (e.g. "1 RM can only have 2 FO/RO").
 */
@Component
@RequiredArgsConstructor
public class HierarchyRulesEngine {

    private final EmployeeHierarchyValidator employeeHierarchyValidator;
    private final ManagerCapacityValidator managerCapacityValidator;

    public void validateEmployeeHierarchy(Employee employee, String managerEmployeeId) {

        employeeHierarchyValidator.validateManagerAssignment(employee, managerEmployeeId);

        if (managerEmployeeId != null && !managerEmployeeId.isBlank()) {
            managerCapacityValidator.validateManagerCapacity(managerEmployeeId, employee.getEmployeeId());
        }
    }
}
