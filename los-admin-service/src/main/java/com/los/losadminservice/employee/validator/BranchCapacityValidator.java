package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BranchCapacityValidator {

    private final EmployeeRepository employeeRepository;

    public void validateBranchCapacity(String branchId, String roleId){

        long roCount = employeeRepository
                .countByBranchIdAndRoleId(branchId,"RELATIONSHIP_OFFICER");

        long rmCount = employeeRepository
                .countByBranchIdAndRoleId(branchId,"RELATIONSHIP_MANAGER");

        if(roleId.equals("RELATIONSHIP_OFFICER") && roCount >= 2){
            throw new RuntimeException(
                    "Branch already has maximum 2 Relationship Officers"
            );
        }

        if(roleId.equals("RELATIONSHIP_MANAGER") && rmCount >= 1){
            throw new RuntimeException(
                    "Branch already has a Relationship Manager"
            );
        }
    }
}