package com.los.losadminservice.employeeBranchMapping.controller;

import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingCreateRequest;
import com.los.losadminservice.employeeBranchMapping.dto.EmployeeBranchMappingResponse;
import com.los.losadminservice.employeeBranchMapping.handler.EmployeeBranchMappingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/employee-branch-mappings")
@RequiredArgsConstructor
public class EmployeeBranchMappingController {

    private final EmployeeBranchMappingHandler handler;

    @PostMapping
    public Object create(
            @RequestBody EmployeeBranchMappingCreateRequest req
    ) {
        return handler.create(req);
    }

    @GetMapping
    public List<EmployeeBranchMappingResponse> getAll() {
        return handler.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeBranchMappingResponse getById(
            @PathVariable Long id
    ) {
        return handler.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deactivate(
            @PathVariable Long id
    ) {
        handler.deactivate(id);
    }
}