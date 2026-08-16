package com.los.losadminservice.employee.controller;

import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.employee.audit.EmployeeManagerHistory;
import com.los.losadminservice.employee.dto.EmployeeClassificationRequest;
import com.los.losadminservice.employee.dto.EmployeeTeamResponse;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.los.losadminservice.employee.dto.EmployeeHierarchyResponse;


@RestController
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final com.los.losadminservice.employee.service.EmployeeApprovalService employeeApprovalService;

    // Get all employees
    @GetMapping
    public List<Employee> getAll(){
        return employeeRepository.findAll();
    }

    // Get employee by employeeId
    @GetMapping("/{employeeId}")
    public Employee getByEmployeeId(@PathVariable String employeeId){

        return employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    // Search managers (autocomplete)
    @GetMapping("/search/search-managers")
    public List<Employee> searchManagers(
            @RequestParam String employeeId
    ){
        return employeeService.searchManagers(employeeId);
    }

    @GetMapping("/search/search-employee")
    public List<Employee> search(
            @RequestParam String q
    ){
        return employeeService.searchEmployee(q);
    }

    /**
     * HR/Admin sets (or changes) an employee's Department + Designation.
     * This is the first step of onboarding an employee into the hierarchy,
     * before a branch mapping or reporting manager can be assigned.
     */
    @PatchMapping("/{employeeId}/classification")
    public Employee assignClassification(
            @PathVariable String employeeId,
            @Valid @RequestBody EmployeeClassificationRequest request
    ){
        return employeeService.assignClassification(
                employeeId,
                request.getDepartmentId(),
                request.getDesignationId()
        );
    }

    // Assign / change manager
    @PatchMapping("/{employeeId}/manager")
    public Employee updateManager(

            @PathVariable String employeeId,
            @RequestParam String managerEmployeeId

    ){

        return employeeService.assignManager(
                employeeId,
                managerEmployeeId
        );

    }

    /**
     * Clears the reporting manager - used when the manager has left, or the
     * employee is temporarily unassigned during a re-org / promotion.
     */
    @DeleteMapping("/{employeeId}/manager")
    public Employee removeManager(
            @PathVariable String employeeId,
            @RequestParam(required = false) String reason
    ){
        return employeeService.removeManager(employeeId, reason);
    }

    @GetMapping("/{employeeId}/manager-history")
    public List<EmployeeManagerHistory> managerHistory(@PathVariable String employeeId){
        return employeeService.getManagerHistory(employeeId);
    }

    @GetMapping("/hierarchy")
    public EmployeeHierarchyResponse hierarchy() {

        return employeeService.getHierarchyTree();
    }

    @GetMapping("/{employeeId}/team")
    public EmployeeTeamResponse team(

            @PathVariable
            String employeeId

    ) {

        return employeeService
                .getTeam(employeeId);
    }

    // ================= Deviation / commercial-approval level =================

    /**
     * Sets the deviation/commercial-approval level + role code an employee
     * is authorised to approve at. Independent of the employee's general
     * RBAC role (roleId/roleName) - this specifically drives how far up
     * the deviation and commercial-matrix approval chain this person can
     * sign off (see loan-originating-system's ApprovalMatrix / rules.enums.UserRole).
     */
    @PatchMapping("/{employeeId}/approval-level")
    public Employee setApprovalLevel(
            @PathVariable String employeeId,
            @jakarta.validation.Valid @RequestBody
            com.los.losadminservice.employee.dto.EmployeeApprovalLevelRequest request
    ) {
        return employeeApprovalService.setApprovalLevel(employeeId, request);
    }

    /**
     * Employees at the given branch authorised to approve as the given
     * role code - used by loan-originating-system to populate Approver 1 /
     * Approver 2 once the Commercial Matrix / deviation rules have
     * resolved which role must approve.
     */
    @GetMapping("/eligible-approvers")
    public List<Employee> eligibleApprovers(
            @RequestParam String branchId,
            @RequestParam String roleCode
    ) {
        return employeeApprovalService.findEligibleApprovers(branchId, roleCode);
    }
}
