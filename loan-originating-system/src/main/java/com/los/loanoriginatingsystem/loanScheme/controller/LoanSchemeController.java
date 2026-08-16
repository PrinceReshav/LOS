package com.los.loanoriginatingsystem.loanScheme.controller;

import com.los.loanoriginatingsystem.loanScheme.dto.LoanSchemeRequest;
import com.los.loanoriginatingsystem.loanScheme.entity.LoanSchemeConfig;
import com.los.loanoriginatingsystem.loanScheme.service.LoanSchemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/loan-schemes")
@RequiredArgsConstructor
public class LoanSchemeController {

    private final LoanSchemeService service;

    @PostMapping
    public LoanSchemeConfig create(@Valid @RequestBody LoanSchemeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<LoanSchemeConfig> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public LoanSchemeConfig update(@PathVariable String id, @Valid @RequestBody LoanSchemeRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public LoanSchemeConfig activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public LoanSchemeConfig deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
