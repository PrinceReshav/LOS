package com.los.losadminservice.employee.validator;

import com.los.losadminservice.common.enums.BranchType;
import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.hierarchyrule.service.HierarchyRuleService;
import com.los.losadminservice.role.model.Role;
import com.los.losadminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * The single place that decides whether "employee X can report to manager Y"
 * is allowed. Every rule it applies is read from configuration
 * (Role flags + HierarchyRule rows) - there is no department-specific
 * branching logic hardcoded in Java, which is what lets Sales, Credit, and
 * every future department share the same engine.
 */
@Component
@RequiredArgsConstructor
public class EmployeeHierarchyValidator {

    private final EmployeeRepository employeeRepository;
    private final RoleService roleService;
    private final HierarchyRuleService hierarchyRuleService;
    private final EmployeeBranchContextResolver branchContextResolver;
    private final BranchManagerAlignmentValidator branchManagerAlignmentValidator;

    public void validateManagerAssignment(Employee employee, String managerEmployeeId) {

        Role employeeRole = roleService.getEntity(employee.getRoleId());

        if (Boolean.TRUE.equals(employeeRole.getIsTopLevel())) {
            throw new BusinessRuleViolationException(
                    employeeRole.getRoleName() +
                            " sits at the top of the org chart and cannot be assigned a reporting manager"
            );
        }

        if (managerEmployeeId == null || managerEmployeeId.isBlank()) {
            // "if not [a manager exists yet] it can be empty" - allowed.
            return;
        }

        if (employee.getEmployeeId().equals(managerEmployeeId)) {
            throw new BusinessRuleViolationException("Employee cannot report to themselves");
        }

        Employee manager = employeeRepository.findByEmployeeId(managerEmployeeId)
                .orElseThrow(() -> new BusinessRuleViolationException("Manager not found: " + managerEmployeeId));

        if (!Boolean.TRUE.equals(manager.getActive())) {
            throw new BusinessRuleViolationException("Manager is not active: " + managerEmployeeId);
        }

        preventCircularReporting(employee.getEmployeeId(), managerEmployeeId);

        Role managerRole = roleService.getEntity(manager.getRoleId());

        String employeeDepartment = employee.getDepartmentId();

        if (employeeDepartment == null || employeeDepartment.isBlank()) {
            throw new BusinessRuleViolationException(
                    "Employee must be assigned a department before a manager can be assigned"
            );
        }

        BranchType employeeBranchContext = branchContextResolver.resolve(employee.getEmployeeId());

        Set<String> allowedManagerRoleIds = hierarchyRuleService.getAllowedManagerRoleIds(
                employeeDepartment,
                employeeRole.getRoleId(),
                employeeBranchContext
        );

        if (allowedManagerRoleIds.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "No hierarchy rule is configured for " + employeeRole.getRoleName() +
                            " in department " + employeeDepartment +
                            ". Ask an admin to configure the allowed reporting roles first."
            );
        }

        if (!allowedManagerRoleIds.contains(managerRole.getRoleId())) {
            throw new BusinessRuleViolationException(
                    employeeRole.getRoleName() + " cannot report to " + managerRole.getRoleName() +
                            " in department " + employeeDepartment
            );
        }

        if (Boolean.TRUE.equals(employeeRole.getRequiresManagerBranchAlign())) {
            branchManagerAlignmentValidator.validateNewManagerCoversAllCurrentBranches(
                    employee.getEmployeeId(),
                    managerEmployeeId,
                    employeeRole.getRoleName()
            );
        }
    }

    /**
     * Prevents assigning a manager who is (directly or transitively) a
     * subordinate of the employee - this would create a reporting cycle,
     * which is exactly the kind of thing that "changing the manager" /
     * "promoting employees" scenarios could otherwise silently create.
     */
    private void preventCircularReporting(String employeeId, String candidateManagerId) {

        Set<String> visited = new HashSet<>();
        String current = candidateManagerId;

        while (current != null) {

            if (current.equals(employeeId)) {
                throw new BusinessRuleViolationException(
                        "This manager assignment would create a reporting cycle"
                );
            }

            if (!visited.add(current)) {
                // already-broken cycle further up the chain - stop, not this call's problem
                break;
            }

            current = employeeRepository.findByEmployeeId(current)
                    .map(Employee::getManagerEmployeeId)
                    .orElse(null);
        }
    }
}
