package com.los.loanoriginatingsystem.systemconfig.controller;

import com.los.loanoriginatingsystem.systemconfig.dto.GeneralConfigRequest;
import com.los.loanoriginatingsystem.systemconfig.entity.GeneralConfig;
import com.los.loanoriginatingsystem.systemconfig.service.SystemConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/general-config")
@RequiredArgsConstructor
public class GeneralConfigController {

    private final SystemConfigService service;

    @PostMapping
    public GeneralConfig create(@Valid @RequestBody GeneralConfigRequest request) {
        return service.createGeneralConfig(request);
    }

    @GetMapping
    public List<GeneralConfig> getAll() {
        return service.getAllGeneralConfig();
    }

    @PutMapping("/{id}")
    public GeneralConfig update(@PathVariable String id, @Valid @RequestBody GeneralConfigRequest request) {
        return service.updateGeneralConfig(id, request);
    }

    @PatchMapping("/{id}/activate")
    public GeneralConfig activate(@PathVariable String id) {
        return service.setGeneralConfigActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public GeneralConfig deactivate(@PathVariable String id) {
        return service.setGeneralConfigActive(id, false);
    }
}
