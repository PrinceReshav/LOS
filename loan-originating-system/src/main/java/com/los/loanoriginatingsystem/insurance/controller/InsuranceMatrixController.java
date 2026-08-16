package com.los.loanoriginatingsystem.insurance.controller;

import com.los.loanoriginatingsystem.insurance.dto.InsuranceMatrixRequest;
import com.los.loanoriginatingsystem.insurance.entity.InsuranceMatrix;
import com.los.loanoriginatingsystem.insurance.service.InsuranceMatrixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/insurance-matrix")
@RequiredArgsConstructor
public class InsuranceMatrixController {

    private final InsuranceMatrixService service;

    @PostMapping
    public InsuranceMatrix create(@Valid @RequestBody InsuranceMatrixRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<InsuranceMatrix> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public InsuranceMatrix update(@PathVariable String id, @Valid @RequestBody InsuranceMatrixRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public InsuranceMatrix activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public InsuranceMatrix deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
