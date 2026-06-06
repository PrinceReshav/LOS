package com.los.losadminservice.employee.validator;

import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BranchCapacityValidator {



    private final EmployeeBranchMappingRepository mappingRepository;
    private final EmployeeRepository employeeRepository;

    public void validateBranchCapacity(
            String branchId,
            String roleId
    ){

        List<EmployeeBranchMapping> mappings =
                mappingRepository.findByBranchIdAndActiveTrue(branchId);

        long roCount = 0;
        long rmCount = 0;

        for(EmployeeBranchMapping mapping : mappings){

            Employee employee =
                    employeeRepository
                            .findByEmployeeId(mapping.getEmployeeId())
                            .orElse(null);

            if(employee == null){
                continue;
            }

            if("RELATIONSHIP_OFFICER".equals(employee.getRoleId())){
                roCount++;
            }

            if("RELATIONSHIP_MANAGER".equals(employee.getRoleId())){
                rmCount++;
            }
        }

        if("RELATIONSHIP_OFFICER".equals(roleId) && roCount >= 2){

            throw new RuntimeException(
                    "Branch already has maximum 2 Relationship Officers"
            );
        }

        if("RELATIONSHIP_MANAGER".equals(roleId) && rmCount >= 1){

            throw new RuntimeException(
                    "Branch already has 1 Relationship Manager"
            );
        }
    }

   /* private final EmployeeRepository employeeRepository;

    *public void validateBranchCapacity(String branchId, String roleId){

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
    }*/
}