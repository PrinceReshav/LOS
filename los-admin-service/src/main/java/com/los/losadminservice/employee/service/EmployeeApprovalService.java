package com.los.losadminservice.employee.service;

import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.employee.dto.EmployeeApprovalLevelRequest;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import com.los.losadminservice.employeeBranchMapping.repository.EmployeeBranchMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assigns/looks up the deviation & commercial-approval level for
 * employees, and answers "which employees at this branch can approve at
 * this role/level" - the query the loan-originating-system's
 * CommercialMatrixService / ApprovalEngine need in order to assign real
 * approver(s) once a matrix lookup has resolved a required role.
 *
 * This is the Java equivalent of the old Salesforce
 * Employee_Branch_Mapping__c + UserRole-hierarchy walk in
 * CommercialMatrixHandler.getUsersInRole(), done as a proper query instead
 * of dynamic SOQL.
 */
@Service
@RequiredArgsConstructor
public class EmployeeApprovalService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeBranchMappingRepository branchMappingRepository;

    @Transactional
    public Employee setApprovalLevel(String employeeId, EmployeeApprovalLevelRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        employee.setApprovalLevel(request.getApprovalLevel());
        employee.setApproverRoleCode(request.getApproverRoleCode().trim().toUpperCase());

        return employeeRepository.save(employee);
    }

    /**
     * All active employees at the given branch who are authorised to
     * approve as the given role code (e.g. "CBM"). Used to populate
     * Approver 1 / Approver 2 once a Commercial Matrix / deviation rule
     * has resolved which role must approve.
     */
    public List<Employee> findEligibleApprovers(String branchId, String roleCode) {

        Set<String> employeeIdsAtBranch = branchMappingRepository
                .findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(EmployeeBranchMapping::getEmployeeId)
                .collect(Collectors.toSet());

        if (employeeIdsAtBranch.isEmpty()) {
            return List.of();
        }

        String normalizedRole = roleCode.trim().toUpperCase();

        return employeeRepository
                .findByApproverRoleCodeAndActiveTrue(normalizedRole)
                .stream()
                .filter(e -> employeeIdsAtBranch.contains(e.getEmployeeId()))
                .toList();
    }
}
