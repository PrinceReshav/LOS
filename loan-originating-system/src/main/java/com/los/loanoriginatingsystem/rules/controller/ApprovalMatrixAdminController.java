package com.los.loanoriginatingsystem.rules.controller;

import com.los.loanoriginatingsystem.rules.dto.ApprovalMatrixRequest;
import com.los.loanoriginatingsystem.rules.entity.ApprovalMatrix;
import com.los.loanoriginatingsystem.rules.service.ApprovalMatrixAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/approval-matrix")
@RequiredArgsConstructor
public class ApprovalMatrixAdminController {

    private final ApprovalMatrixAdminService service;

    @PostMapping
    public ApprovalMatrix create(@Valid @RequestBody ApprovalMatrixRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ApprovalMatrix> getAll() {
        return service.getAll();
    }

    @GetMapping("/level/{level}")
    public List<ApprovalMatrix> getByLevel(@PathVariable Integer level) {
        return service.getByLevel(level);
    }

    @PutMapping("/{id}")
    public ApprovalMatrix update(@PathVariable String id, @Valid @RequestBody ApprovalMatrixRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public ApprovalMatrix activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public ApprovalMatrix deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
