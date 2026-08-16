package com.los.loanoriginatingsystem.systemconfig.controller;

import com.los.loanoriginatingsystem.systemconfig.dto.StampDutyConfigRequest;
import com.los.loanoriginatingsystem.systemconfig.entity.StampDutyConfig;
import com.los.loanoriginatingsystem.systemconfig.service.SystemConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/stamp-duty-config")
@RequiredArgsConstructor
public class StampDutyConfigController {

    private final SystemConfigService service;

    @PostMapping
    public StampDutyConfig create(@Valid @RequestBody StampDutyConfigRequest request) {
        return service.createStampDuty(request);
    }

    @GetMapping
    public List<StampDutyConfig> getAll() {
        return service.getAllStampDuty();
    }

    @GetMapping("/state/{stateCode}")
    public StampDutyConfig getByState(@PathVariable String stateCode) {
        return service.getStampDutyByState(stateCode);
    }

    @PutMapping("/{id}")
    public StampDutyConfig update(@PathVariable String id, @Valid @RequestBody StampDutyConfigRequest request) {
        return service.updateStampDuty(id, request);
    }

    @PatchMapping("/{id}/activate")
    public StampDutyConfig activate(@PathVariable String id) {
        return service.setStampDutyActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public StampDutyConfig deactivate(@PathVariable String id) {
        return service.setStampDutyActive(id, false);
    }
}
