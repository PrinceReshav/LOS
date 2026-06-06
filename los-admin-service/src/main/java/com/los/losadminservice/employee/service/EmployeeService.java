package com.los.losadminservice.employee.service;

import com.los.events.UserCreatedEvent;
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

                .roleId(event.roleId())
                .roleName(event.roleName())

                .profileId(event.profileId())
                .profileName(event.profileName())

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

    @Transactional
    public Employee assignManager(String employeeId, String managerEmployeeId){

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Employee manager = employeeRepository
                .findByEmployeeId(managerEmployeeId)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        if(employee.getEmployeeId().equals(managerEmployeeId)){
            throw new RuntimeException("Employee cannot be their own manager");
        }

        // HIERARCHY VALIDATION ENGINE
        hierarchyRulesEngine.validateEmployeeHierarchy(
                employee,
                managerEmployeeId
        );

        employee.setManagerEmployeeId(managerEmployeeId);

        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeTeamResponse getTeam(
            String employeeId
    ) {

        Employee employee =
                employeeRepository
                        .findByEmployeeId(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

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