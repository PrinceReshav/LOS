package com.los.losadminservice.employee.controller;

import com.los.losadminservice.employee.dto.EmployeeTeamResponse;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import com.los.losadminservice.employee.service.EmployeeService;
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
                .orElseThrow(() -> new RuntimeException("Employee not found"));
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

    // Assign manager
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
}