package com.los.loanoriginatingsystem.integration.config.controller;

import com.los.loanoriginatingsystem.integration.config.dto.IntegrationConfigRequest;
import com.los.loanoriginatingsystem.integration.config.dto.IntegrationConfigResponse;
import com.los.loanoriginatingsystem.integration.config.service.IntegrationConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin management of outbound integration configs (base URL, timeout,
 * auth strategy, API key/secret). Equivalent to managing
 * HTTPCalloutConfiguration__mdt records in the old Salesforce org, but
 * live/editable without a deployment.
 */
@RestController
@RequestMapping("/admin/integration-configs")
@RequiredArgsConstructor
public class IntegrationConfigController {

    private final IntegrationConfigService service;

    @PostMapping
    public IntegrationConfigResponse create(@Valid @RequestBody IntegrationConfigRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<IntegrationConfigResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public IntegrationConfigResponse get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public IntegrationConfigResponse update(
            @PathVariable String id,
            @Valid @RequestBody IntegrationConfigRequest request
    ) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public IntegrationConfigResponse activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public IntegrationConfigResponse deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
