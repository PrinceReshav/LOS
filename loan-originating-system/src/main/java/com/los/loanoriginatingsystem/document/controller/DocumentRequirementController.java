package com.los.loanoriginatingsystem.document.controller;


import com.los.loanoriginatingsystem.document.dto.RequiredDocumentResponseDTO;
import com.los.loanoriginatingsystem.document.service.DocumentRequirementService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentRequirementController {

    private final DocumentRequirementService service;

    // =====================================================
    // 🔥 REQUIRED DOCUMENT API (FRONTEND DRIVER)
    // =====================================================
    @GetMapping("/required/{tempId}")
    public RequiredDocumentResponseDTO getRequiredDocuments(
            @PathVariable String tempId
    ) {
        return service.getRequiredDocuments(tempId);
    }
}