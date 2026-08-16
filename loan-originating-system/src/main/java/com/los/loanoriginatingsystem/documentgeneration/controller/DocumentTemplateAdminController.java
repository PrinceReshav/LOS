package com.los.loanoriginatingsystem.documentgeneration.controller;

import com.los.loanoriginatingsystem.documentgeneration.dto.DocumentTemplateRequest;
import com.los.loanoriginatingsystem.documentgeneration.entity.DocumentTemplate;
import com.los.loanoriginatingsystem.documentgeneration.service.DocumentTemplateAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin CRUD + activation for document-generation templates. */
@RestController
@RequestMapping("/admin/document-templates")
@RequiredArgsConstructor
public class DocumentTemplateAdminController {

    private final DocumentTemplateAdminService service;

    @PostMapping
    public DocumentTemplate create(@Valid @RequestBody DocumentTemplateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DocumentTemplate> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DocumentTemplate get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public DocumentTemplate update(@PathVariable String id, @Valid @RequestBody DocumentTemplateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public DocumentTemplate activate(@PathVariable String id) {
        return service.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public DocumentTemplate deactivate(@PathVariable String id) {
        return service.setActive(id, false);
    }
}
