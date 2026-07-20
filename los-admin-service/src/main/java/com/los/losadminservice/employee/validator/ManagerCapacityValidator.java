package com.los.losadminservice.employee.validator;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.role.model.Role;
import com.los.losadminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Enforces: "1 Relationship Manager can only have 2 Field Officers or
 * Relationship Officers [reporting to them]". The cap is configured on the
 * MANAGER's role (Role.maxDirectReports) so it generalizes to any
 * department/role without a code change.
 */
@Component
@RequiredArgsConstructor
public class ManagerCapacityValidator {

    private final EmployeeRepository employeeRepository;
    private final RoleService roleService;

    public void validateManagerCapacity(String managerEmployeeId, String movingEmployeeId) {

        Employee manager = employeeRepository.findByEmployeeId(managerEmployeeId)
                .orElseThrow(() -> new BusinessRuleViolationException("Manager not found"));

        Role managerRole = roleService.getEntity(manager.getRoleId());

        if (managerRole.getMaxDirectReports() == null) {
            return;
        }

        List<Employee> currentDirectReports =
                employeeRepository.findByManagerEmployeeId(managerEmployeeId);

        long currentCount = currentDirectReports.stream()
                .filter(e -> !e.getEmployeeId().equals(movingEmployeeId))
                .count();

        if (currentCount >= managerRole.getMaxDirectReports()) {
            throw new BusinessRuleViolationException(
                    manager.getFullName() + " (" + managerRole.getRoleName() + ") already has the maximum of "
                            + managerRole.getMaxDirectReports() + " direct reports"
            );
        }
    }
}
