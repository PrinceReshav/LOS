package com.los.loanoriginatingsystem.rules.controller;

import com.los.loanoriginatingsystem.rules.dto.RuleCriteriaRequest;
import com.los.loanoriginatingsystem.rules.dto.RuleEngineRequest;
import com.los.loanoriginatingsystem.rules.entity.RuleCriteria;
import com.los.loanoriginatingsystem.rules.entity.RuleEngine;
import com.los.loanoriginatingsystem.rules.service.RuleEngineAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Business Rule (deviation rule) admin console: create/edit rules, manage
 * their criteria, and activate/deactivate without redeploying.
 */
@RestController
@RequestMapping("/admin/business-rules")
@RequiredArgsConstructor
public class RuleEngineAdminController {

    private final RuleEngineAdminService service;

    @PostMapping
    public RuleEngine create(@Valid @RequestBody RuleEngineRequest request) {
        return service.createRule(request);
    }

    @GetMapping
    public List<RuleEngine> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RuleEngine get(@PathVariable String id) {
        return service.getRule(id);
    }

    @GetMapping("/by-object/{objectApiName}")
    public List<RuleEngine> getByObject(@PathVariable String objectApiName) {
        return service.getByObjectApiName(objectApiName);
    }

    @PutMapping("/{id}")
    public RuleEngine update(@PathVariable String id, @Valid @RequestBody RuleEngineRequest request) {
        return service.updateRule(id, request);
    }

    @PatchMapping("/{id}/activate")
    public RuleEngine activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public RuleEngine deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteRule(id);
    }

    // ================= Criteria =================

    @GetMapping("/{ruleId}/criteria")
    public List<RuleCriteria> getCriteria(@PathVariable String ruleId) {
        return service.getCriteria(ruleId);
    }

    @PostMapping("/{ruleId}/criteria")
    public RuleCriteria addCriteria(
            @PathVariable String ruleId,
            @Valid @RequestBody RuleCriteriaRequest request
    ) {
        return service.addCriteria(ruleId, request);
    }

    @PutMapping("/{ruleId}/criteria/{criteriaId}")
    public RuleCriteria updateCriteria(
            @PathVariable String ruleId,
            @PathVariable String criteriaId,
            @Valid @RequestBody RuleCriteriaRequest request
    ) {
        return service.updateCriteria(ruleId, criteriaId, request);
    }

    @DeleteMapping("/{ruleId}/criteria/{criteriaId}")
    public void deleteCriteria(@PathVariable String ruleId, @PathVariable String criteriaId) {
        service.deleteCriteria(ruleId, criteriaId);
    }
}
