package com.los.losadminservice.designation.controller;

import com.los.losadminservice.designation.dto.DesignationRequest;
import com.los.losadminservice.designation.dto.DesignationResponse;
import com.los.losadminservice.designation.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    @PostMapping
    public DesignationResponse create(@Valid @RequestBody DesignationRequest request) {
        return designationService.create(request);
    }

    @GetMapping("/{designationId}")
    public DesignationResponse get(@PathVariable String designationId) {
        return designationService.get(designationId);
    }

    @GetMapping
    public List<DesignationResponse> getAll(
            @RequestParam(required = false) String departmentCode,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return designationService.getAll(departmentCode, activeOnly);
    }

    @PutMapping("/{designationId}")
    public DesignationResponse update(
            @PathVariable String designationId,
            @RequestBody DesignationRequest request
    ) {
        return designationService.update(designationId, request);
    }
}
