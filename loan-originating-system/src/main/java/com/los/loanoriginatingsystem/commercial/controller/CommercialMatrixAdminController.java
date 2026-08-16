package com.los.loanoriginatingsystem.commercial.controller;

import com.los.loanoriginatingsystem.commercial.dto.CommercialMatrixRequest;
import com.los.loanoriginatingsystem.commercial.entity.CommercialMatrix;
import com.los.loanoriginatingsystem.commercial.service.CommercialMatrixAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin CRUD + activation for Commercial Matrix (approval-routing) rows. */
@RestController
@RequestMapping("/admin/commercial-matrix")
@RequiredArgsConstructor
public class CommercialMatrixAdminController {

    private final CommercialMatrixAdminService service;

    @PostMapping
    public CommercialMatrix create(@Valid @RequestBody CommercialMatrixRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<CommercialMatrix> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CommercialMatrix get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public CommercialMatrix update(@PathVariable String id, @Valid @RequestBody CommercialMatrixRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public CommercialMatrix activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public CommercialMatrix deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
