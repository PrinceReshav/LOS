package com.los.losadminservice.employee.service;

import com.los.events.UserCreatedEvent;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.validator.HierarchyRulesEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .profileId(event.profileId())

                .managerEmployeeId(null)
                .branchId(null)
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
}