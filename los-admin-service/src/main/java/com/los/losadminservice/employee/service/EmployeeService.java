package com.los.losadminservice.employee.service;

import com.los.events.UserCreatedEvent;
import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.department.service.DepartmentService;
import com.los.losadminservice.department.model.Department;
import com.los.losadminservice.designation.service.DesignationService;
import com.los.losadminservice.designation.model.Designation;
import com.los.losadminservice.employee.audit.EmployeeManagerHistory;
import com.los.losadminservice.employee.audit.EmployeeManagerHistoryRepository;
import com.los.losadminservice.employee.audit.ManagerChangeType;
import com.los.losadminservice.employee.dto.EmployeeTeamResponse;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.validator.HierarchyRulesEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.los.losadminservice.employee.dto.EmployeeHierarchyResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final HierarchyRulesEngine hierarchyRulesEngine;
    private final EmployeeManagerHistoryRepository managerHistoryRepository;
    private final DepartmentService departmentService;
    private final DesignationService designationService;

    @Transactional
    public Employee createEmployeeFromUserEvent(UserCreatedEvent event){

        if(employeeRepository.existsByEmployeeId(event.employeeId())
                || employeeRepository.existsByUserId(event.userId())) {

            log.warn(
                    "Duplicate Kafka event ignored employeeId={} userId={}",
                    event.employeeId(),
                    event.userId()
            );

            return employeeRepository
                    .findByEmployeeId(event.employeeId())
                    .orElse(null);
        }

        Employee emp = Employee.builder()

                .userId(event.userId())
                .employeeId(event.employeeId())

                .fullName((event.firstName() + " " + event.lastName()).trim())

                .email(event.email())
                .mobile(event.mobile())

                // FIX: was event.roleId()/event.roleName() -
                // administration-service's system-access role
                // (role_admin/role_sales), which doesn't exist in this
                // service's own role catalog and caused "Role not
                // found" downstream in EmployeeHierarchyValidator. Use
                // the organizational role instead, same as the gRPC
                // path in EmployeeGrpcService.
                .roleId(event.orgRoleId())
                .roleName(event.orgRoleName())

                .profileId(event.profileId())
                .profileName(event.profileName())

                // Department / Designation are NOT part of the User-service
                // contract - HR/Admin assigns them explicitly afterwards via
                // assignClassification(), which is also where the manager
                // and branch mapping flow begins.
                .departmentId(null)
                .departmentName(null)
                .designationId(null)
                .designationName(null)

                .managerEmployeeId(null)
                .active(true)

                .build();

        Employee saved = employeeRepository.save(emp);

        log.info(
                "EMPLOYEE_CREATED | employeeId={} | userId={}",
                event.employeeId(),
                event.userId()
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Employee> searchManagers(String employeeId){

        return employeeRepository
                .findByEmployeeIdContainingIgnoreCase(employeeId);
    }

    @Transactional(readOnly = true)
    public List<Employee> searchEmployee(String q){

        return employeeRepository
                .findByEmployeeIdContainingIgnoreCaseOrFullNameContainingIgnoreCase(q,q);
    }

    /**
     * HR/Admin assigns (or changes) the Department + Designation for an
     * employee. Required before a reporting manager or branch mapping can
     * be created, since the hierarchy engine is department-scoped.
     *
     * If the employee's department changes and their existing manager is no
     * longer valid under the new department's hierarchy rules, the manager
     * is cleared (never left silently inconsistent) and logged - the caller
     * / frontend should then prompt for a fresh manager assignment.
     */
    @Transactional
    public Employee assignClassification(String employeeId, String departmentId, String designationId) {

        Employee employee = getEmployeeOrThrow(employeeId);

        Department department = departmentService.getEntity(departmentId);
        Designation designation = designationService.getEntity(designationId);

        if (designation.getDepartmentCode() != null
                && !designation.getDepartmentCode().equalsIgnoreCase(department.getCode())) {
            throw new BusinessRuleViolationException(
                    "Designation " + designation.getName() + " does not belong to department " + department.getName()
            );
        }

        employee.setDepartmentId(department.getCode());
        employee.setDepartmentName(department.getName());
        employee.setDesignationId(designation.getDesignationId());
        employee.setDesignationName(designation.getName());

        if (employee.getManagerEmployeeId() != null) {

            try {
                hierarchyRulesEngine.validateEmployeeHierarchy(employee, employee.getManagerEmployeeId());
            } catch (RuntimeException ex) {

                log.warn(
                        "Clearing manager for employeeId={} after classification change: {}",
                        employeeId, ex.getMessage()
                );

                recordManagerHistory(
                        employeeId,
                        employee.getManagerEmployeeId(),
                        null,
                        ManagerChangeType.REMOVED,
                        "Auto-cleared after department/designation change: " + ex.getMessage()
                );

                employee.setManagerEmployeeId(null);
            }
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee assignManager(String employeeId, String managerEmployeeId){

        Employee employee = getEmployeeOrThrow(employeeId);

        String oldManagerId = employee.getManagerEmployeeId();

        // HIERARCHY VALIDATION ENGINE
        hierarchyRulesEngine.validateEmployeeHierarchy(
                employee,
                managerEmployeeId
        );

        employee.setManagerEmployeeId(managerEmployeeId);
        Employee saved = employeeRepository.save(employee);

        recordManagerHistory(
                employeeId,
                oldManagerId,
                managerEmployeeId,
                oldManagerId == null ? ManagerChangeType.ASSIGNED : ManagerChangeType.CHANGED,
                null
        );

        return saved;
    }

    /**
     * Clears an employee's manager (e.g. the manager left, or the employee
     * is between reporting lines during a re-org). Explicitly supported so
     * "if [a manager] is not [present] it can be empty" always has a safe,
     * intentional path rather than requiring a dummy manager id.
     */
    @Transactional
    public Employee removeManager(String employeeId, String reason){

        Employee employee = getEmployeeOrThrow(employeeId);

        String oldManagerId = employee.getManagerEmployeeId();

        if (oldManagerId == null) {
            return employee;
        }

        employee.setManagerEmployeeId(null);
        Employee saved = employeeRepository.save(employee);

        recordManagerHistory(employeeId, oldManagerId, null, ManagerChangeType.REMOVED, reason);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<EmployeeManagerHistory> getManagerHistory(String employeeId){

        return managerHistoryRepository.findByEmployeeIdOrderByChangedAtDesc(employeeId);
    }

    private void recordManagerHistory(
            String employeeId,
            String oldManagerEmployeeId,
            String newManagerEmployeeId,
            ManagerChangeType changeType,
            String reason
    ) {

        managerHistoryRepository.save(
                EmployeeManagerHistory.builder()
                        .employeeId(employeeId)
                        .oldManagerEmployeeId(oldManagerEmployeeId)
                        .newManagerEmployeeId(newManagerEmployeeId)
                        .changeType(changeType)
                        .reason(reason)
                        .build()
        );
    }

    private Employee getEmployeeOrThrow(String employeeId) {

        return employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    @Transactional(readOnly = true)
    public EmployeeTeamResponse getTeam(
            String employeeId
    ) {

        Employee employee = getEmployeeOrThrow(employeeId);

        List<Employee> managerChain =
                buildManagerChain(employee);

        List<Employee> peers =
                buildPeers(employee);

        List<Employee> subordinates =
                buildSubordinates(employee);

        return EmployeeTeamResponse.builder()
                .employee(employee)
                .managerChain(managerChain)
                .peers(peers)
                .subordinates(subordinates)
                .build();
    }

    @Transactional(readOnly = true)
    public EmployeeHierarchyResponse getHierarchyTree() {

        List<Employee> employees =
                employeeRepository.findAll();

        Map<String, EmployeeHierarchyResponse> map =
                new HashMap<>();

        // STEP 1 -> CREATE NODES
        for (Employee emp : employees) {

            map.put(
                    emp.getEmployeeId(),

                    EmployeeHierarchyResponse.builder()
                            .employeeId(emp.getEmployeeId())
                            .fullName(emp.getFullName())
                            .roleName(emp.getRoleName())
                            .managerEmployeeId(emp.getManagerEmployeeId())
                            .children(new ArrayList<>())
                            .build()
            );
        }

        EmployeeHierarchyResponse root = null;

        // STEP 2 -> BUILD TREE
        for (Employee emp : employees) {

            EmployeeHierarchyResponse current =
                    map.get(emp.getEmployeeId());

            if (emp.getManagerEmployeeId() == null) {

                root = current;

            } else {

                EmployeeHierarchyResponse manager =
                        map.get(emp.getManagerEmployeeId());

                if (manager != null) {

                    manager.getChildren().add(current);
                }
            }
        }

        return root;
    }

    private List<Employee> buildManagerChain(
            Employee employee
    ) {

        List<Employee> result =
                new ArrayList<>();

        String managerId =
                employee.getManagerEmployeeId();

        while(managerId != null){

            Employee manager =
                    employeeRepository
                            .findByEmployeeId(managerId)
                            .orElse(null);

            if(manager == null){
                break;
            }

            result.add(manager);

            managerId =
                    manager.getManagerEmployeeId();
        }

        return result;
    }

    private List<Employee> buildPeers(
            Employee employee
    ) {

        if(employee.getManagerEmployeeId() == null){
            return List.of();
        }

        return employeeRepository
                .findByManagerEmployeeId(
                        employee.getManagerEmployeeId()
                )
                .stream()
                .filter(e ->
                        !e.getEmployeeId()
                                .equals(employee.getEmployeeId())
                )
                .toList();
    }

    private List<Employee> buildSubordinates(
            Employee employee
    ) {

        List<Employee> result =
                new ArrayList<>();

        collectSubordinates(
                employee.getEmployeeId(),
                result
        );

        return result;
    }

    private void collectSubordinates(
            String managerId,
            List<Employee> result
    ) {

        List<Employee> directReports =
                employeeRepository
                        .findByManagerEmployeeId(
                                managerId
                        );

        for(Employee emp : directReports){

            result.add(emp);

            collectSubordinates(
                    emp.getEmployeeId(),
                    result
            );
        }
    }

}