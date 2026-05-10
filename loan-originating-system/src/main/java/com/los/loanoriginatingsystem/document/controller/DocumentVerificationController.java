package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.service.DocumentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentVerificationController {

    private final DocumentVerificationService service;

    // =====================================================
    // 🔍 VERIFY DOCUMENT
    // =====================================================
    @PostMapping("/verify/{documentId}")
    public Map<String, Object> verify(@PathVariable String documentId) {

        Object response = service.verifyDocument(documentId);

        return Map.of(
                "status", "SUCCESS",
                "data", response
        );
    }
}