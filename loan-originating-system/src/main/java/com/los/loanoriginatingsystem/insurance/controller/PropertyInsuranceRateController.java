package com.los.loanoriginatingsystem.insurance.controller;

import com.los.loanoriginatingsystem.insurance.dto.PropertyInsuranceRateRequest;
import com.los.loanoriginatingsystem.insurance.entity.PropertyInsuranceRate;
import com.los.loanoriginatingsystem.insurance.service.InsuranceMatrixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/property-insurance-rates")
@RequiredArgsConstructor
public class PropertyInsuranceRateController {

    private final InsuranceMatrixService service;

    @PostMapping
    public PropertyInsuranceRate create(@Valid @RequestBody PropertyInsuranceRateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<PropertyInsuranceRate> getAll() {
        return service.getAllPropertyRates();
    }

    @PutMapping("/{id}")
    public PropertyInsuranceRate update(@PathVariable String id, @Valid @RequestBody PropertyInsuranceRateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public PropertyInsuranceRate activate(@PathVariable String id) {
        return service.setPropertyInsuranceRateActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public PropertyInsuranceRate deactivate(@PathVariable String id) {
        return service.setPropertyInsuranceRateActive(id, false);
    }
}
