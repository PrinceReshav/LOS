package com.los.losadminservice.department.service;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.department.dto.DepartmentRequest;
import com.los.losadminservice.department.dto.DepartmentResponse;
import com.los.losadminservice.department.model.Department;
import com.los.losadminservice.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD service for the Department master table.
 *
 * This is intentionally simple: departments are plain reference data.
 * All hierarchy behaviour that differs department-to-department lives in
 * the HierarchyRule engine, not here, so new departments never require a
 * code change - only a row in this table plus role / hierarchy-rule rows.
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {

        String code = request.getCode().trim().toUpperCase();

        if (departmentRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessRuleViolationException(
                    "Department already exists: " + code
            );
        }

        Department department = Department.builder()
                .code(code)
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse update(String code, DepartmentRequest request) {

        Department department = getEntity(code);

        if (request.getName() != null) {
            department.setName(request.getName());
        }

        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            department.setActive(request.getActive());
        }

        return toResponse(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public DepartmentResponse get(String code) {
        return toResponse(getEntity(code));
    }

    @Transactional(readOnly = true)
    public Department getEntity(String code) {

        return departmentRepository.findById(code.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found: " + code
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll(boolean activeOnly) {

        List<Department> departments = activeOnly
                ? departmentRepository.findByActiveTrue()
                : departmentRepository.findAll();

        return departments.stream()
                .map(this::toResponse)
                .toList();
    }

    private DepartmentResponse toResponse(Department department) {

        return DepartmentResponse.builder()
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .active(department.getActive())
                .build();
    }
}
