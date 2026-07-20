package com.los.losadminservice.hierarchyrule.controller;

import com.los.losadminservice.hierarchyrule.dto.HierarchyRuleRequest;
import com.los.losadminservice.hierarchyrule.dto.HierarchyRuleResponse;
import com.los.losadminservice.hierarchyrule.service.HierarchyRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Master-data CRUD for hierarchy rules - the configuration surface for the
 * "who can report to whom" engine. Adding, say, a new "Territory Credit
 * Manager" step to the Credit chain is a POST here, not a deployment.
 */
@RestController
@RequestMapping("/admin/hierarchy-rules")
@RequiredArgsConstructor
public class HierarchyRuleController {

    private final HierarchyRuleService hierarchyRuleService;

    @PostMapping
    public HierarchyRuleResponse create(@Valid @RequestBody HierarchyRuleRequest request) {
        return hierarchyRuleService.create(request);
    }

    @PutMapping("/{id}")
    public HierarchyRuleResponse update(
            @PathVariable Long id,
            @RequestBody HierarchyRuleRequest request
    ) {
        return hierarchyRuleService.update(id, request);
    }

    @GetMapping
    public List<HierarchyRuleResponse> getAll(
            @RequestParam(required = false) String departmentCode
    ) {
        return departmentCode != null
                ? hierarchyRuleService.getByDepartment(departmentCode)
                : hierarchyRuleService.getAll();
    }
}
