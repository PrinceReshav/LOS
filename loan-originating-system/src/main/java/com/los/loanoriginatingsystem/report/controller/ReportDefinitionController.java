package com.los.loanoriginatingsystem.report.controller;

import com.los.loanoriginatingsystem.report.dto.ReportDefinitionRequest;
import com.los.loanoriginatingsystem.report.dto.ReportDefinitionResponse;
import com.los.loanoriginatingsystem.report.dto.ReportExecutionResult;
import com.los.loanoriginatingsystem.report.enums.ReportSourceObject;
import com.los.loanoriginatingsystem.report.registry.ReportSourceRegistry;
import com.los.loanoriginatingsystem.report.service.ReportDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportDefinitionController {

    private final ReportDefinitionService service;
    private final ReportSourceRegistry sourceRegistry;

    @GetMapping
    public List<ReportDefinitionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReportDefinitionResponse getById(
            @PathVariable String id
    ) {
        return service.getById(id);
    }

    @PostMapping
    public ReportDefinitionResponse create(
            @RequestBody ReportDefinitionRequest request
    ) {
        return service.create(request, currentUser());
    }

    @PutMapping("/{id}")
    public ReportDefinitionResponse update(
            @PathVariable String id,
            @RequestBody ReportDefinitionRequest request
    ) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/clone")
    public ReportDefinitionResponse clone(
            @PathVariable String id
    ) {
        return service.clone(id, currentUser());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/{id}/execute")
    public ReportExecutionResult execute(
            @PathVariable String id
    ) {
        return service.execute(id);
    }

    @PostMapping("/execute-adhoc")
    public ReportExecutionResult executeAdhoc(
            @RequestBody ReportDefinitionRequest request
    ) {
        return service.executeAdhoc(request);
    }

    // Powers the report builder UI's field picker — lists every
    // reportable field for a given source object.
    @GetMapping("/sources/{source}/fields")
    public List<String> describeFields(
            @PathVariable ReportSourceObject source
    ) {
        return sourceRegistry.describeFields(source);
    }

    @GetMapping("/sources")
    public List<ReportSourceObject> listSources() {
        return List.of(ReportSourceObject.values());
    }

    private String currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        return authentication.getName();
    }
}
