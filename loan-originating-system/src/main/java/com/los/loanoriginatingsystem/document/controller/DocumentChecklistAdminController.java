package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.dto.DocumentChecklistRequest;
import com.los.loanoriginatingsystem.document.entity.DocumentChecklist;
import com.los.loanoriginatingsystem.document.service.DocumentChecklistAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/document-checklist")
@RequiredArgsConstructor
public class DocumentChecklistAdminController {

    private final DocumentChecklistAdminService service;

    @PostMapping
    public DocumentChecklist create(@Valid @RequestBody DocumentChecklistRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DocumentChecklist> getAll() {
        return service.getAll();
    }

    @GetMapping("/product/{loanProductCode}")
    public List<DocumentChecklist> getByProduct(@PathVariable String loanProductCode) {
        return service.getByProductCode(loanProductCode);
    }

    @PutMapping("/{id}")
    public DocumentChecklist update(@PathVariable String id, @Valid @RequestBody DocumentChecklistRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public DocumentChecklist activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public DocumentChecklist deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
