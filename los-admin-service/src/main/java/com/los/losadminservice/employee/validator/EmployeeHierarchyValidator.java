package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeHierarchyValidator {

    private final EmployeeRepository employeeRepository;

    public void validateManagerAssignment(
            Employee employee,
            String managerEmployeeId
    ){

        if(employee.getEmployeeId().equals(managerEmployeeId)){
            throw new RuntimeException(
                    "Employee cannot report to themselves"
            );
        }

        Employee manager = employeeRepository
                .findByEmployeeId(managerEmployeeId)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found")
                );

        String employeeRole = employee.getRoleId();
        String managerRole = manager.getRoleId();

        List<String> allowedManagers =
                HierarchyRoleRules.ALLOWED_MANAGERS
                        .get(employeeRole);

        if(allowedManagers != null
                && !allowedManagers.contains(managerRole)){

            throw new RuntimeException(
                    employeeRole +
                            " cannot report to " +
                            managerRole
            );
        }
    }
}