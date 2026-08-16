package com.los.loanoriginatingsystem.documentgeneration.controller;

import com.los.loanoriginatingsystem.documentgeneration.dto.GenerateDocumentRequest;
import com.los.loanoriginatingsystem.documentgeneration.dto.GenerateDocumentResponse;
import com.los.loanoriginatingsystem.documentgeneration.service.DocumentGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** Triggers generation of a specific document (by template code) for a loan application. */
@RestController
@RequestMapping("/api/loan-applications/{loanApplicationId}/documents")
@RequiredArgsConstructor
public class DocumentGenerationController {

    private final DocumentGenerationService service;

    @PostMapping("/generate/{templateCode}")
    public GenerateDocumentResponse generate(
            @PathVariable String loanApplicationId,
            @PathVariable String templateCode,
            @RequestBody(required = false) GenerateDocumentRequest request
    ) {
        return service.generate(loanApplicationId, templateCode, request);
    }
}
