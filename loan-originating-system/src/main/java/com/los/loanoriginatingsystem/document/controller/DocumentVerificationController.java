package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.dto.DocumentVerificationResponse;
import com.los.loanoriginatingsystem.document.service.DocumentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentVerificationController {

    private final DocumentVerificationService service;

    @PostMapping("/verify/{documentId}")
    public DocumentVerificationResponse verify(
            @PathVariable String documentId
    ) {
        return service.verify(documentId);
    }
}