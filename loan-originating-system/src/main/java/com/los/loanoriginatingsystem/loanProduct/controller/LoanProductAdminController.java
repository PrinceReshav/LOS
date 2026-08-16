package com.los.loanoriginatingsystem.loanProduct.controller;

import com.los.loanoriginatingsystem.loanProduct.dto.LoanProductRequest;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin CRUD + activation for Loan Products. The read-only, dependent-picklist
 * style lookups used during loan origination itself (getProductsByLoanType,
 * getProductConfig) remain on LoanProductController - this controller is
 * the "product configuration" screen: create/edit a product, and turn it
 * on/off without deleting its history.
 */
@RestController
@RequestMapping("/admin/loan-products")
@RequiredArgsConstructor
public class LoanProductAdminController {

    private final LoanProductService service;

    @PostMapping
    public LoanProduct create(@Valid @RequestBody LoanProductRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<LoanProduct> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LoanProduct get(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public LoanProduct update(@PathVariable String id, @Valid @RequestBody LoanProductRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public LoanProduct activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public LoanProduct deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
