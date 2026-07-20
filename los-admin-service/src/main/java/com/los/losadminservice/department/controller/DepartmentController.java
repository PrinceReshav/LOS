package com.los.losadminservice.department.controller;

import com.los.losadminservice.department.dto.DepartmentRequest;
import com.los.losadminservice.department.dto.DepartmentResponse;
import com.los.losadminservice.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Master-data CRUD for Departments.
 * Frontend uses this to populate department dropdowns when creating
 * roles, designations, hierarchy rules and when classifying an employee.
 */
@RestController
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @GetMapping("/{code}")
    public DepartmentResponse get(@PathVariable String code) {
        return departmentService.get(code);
    }

    @GetMapping
    public List<DepartmentResponse> getAll(
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return departmentService.getAll(activeOnly);
    }

    @PutMapping("/{code}")
    public DepartmentResponse update(
            @PathVariable String code,
            @RequestBody DepartmentRequest request
    ) {
        return departmentService.update(code, request);
    }
}
