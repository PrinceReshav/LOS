package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HierarchyRulesEngine {

    private final BranchCapacityValidator branchCapacityValidator;
    private final EmployeeHierarchyValidator employeeHierarchyValidator;

    public void validateEmployeeHierarchy(
            Employee employee,
            String managerEmployeeId
    ){
        if(managerEmployeeId != null){
            employeeHierarchyValidator
                    .validateManagerAssignment(
                            employee,
                            managerEmployeeId
                    );
        }
    }
}