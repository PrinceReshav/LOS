package com.los.losadminservice.employeeBranchMapping.controller;

import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingCreateRequest;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.handler.EmployeeBranchMappingHandler;
import com.los.losadminservice.employeeBranchMapping.model.EmployeeBranchMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A branch can be mapped to an employee either from here directly, or - per
 * the product requirement - from the Employee Details page, which calls the
 * same create() endpoint under the hood.
 */
@RestController
@RequestMapping("/admin/employee-branch-mappings")
@RequiredArgsConstructor
public class EmployeeBranchMappingController {

    private final EmployeeBranchMappingHandler handler;

    @PostMapping
    public EmployeeBranchMapping create(
            @Valid @RequestBody EmployeeBranchMappingCreateRequest req
    ) {
        return handler.create(req);
    }

    @GetMapping
    public List<EmployeeBranchMappingResponse> getAll() {
        return handler.getAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<EmployeeBranchMappingResponse> getForEmployee(@PathVariable String employeeId) {
        return handler.getForEmployee(employeeId);
    }

    @GetMapping("/{id}")
    public EmployeeBranchMappingResponse getById(
            @PathVariable Long id
    ) {
        return handler.getById(id);
    }

    @PatchMapping("/{id}/primary")
    public EmployeeBranchMapping setPrimary(@PathVariable Long id) {
        return handler.setPrimary(id);
    }

    @DeleteMapping("/{id}")
    public void deactivate(
            @PathVariable Long id
    ) {
        handler.deactivate(id);
    }
}
