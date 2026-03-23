package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        if(employeeRole.equals("RELATIONSHIP_OFFICER")){

            if(!managerRole.equals("RELATIONSHIP_MANAGER")){
                throw new RuntimeException(
                        "Relationship Officer must report to Relationship Manager"
                );
            }

        }

        if(employeeRole.equals("RELATIONSHIP_MANAGER")){

            if(managerRole.equals("RELATIONSHIP_OFFICER")){
                throw new RuntimeException(
                        "Relationship Manager cannot report to RO"
                );
            }
        }
    }
}