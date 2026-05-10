package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.service.KYCDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentUploadController {

    private final KYCDocumentService service;

    // =====================================================
    // 📤 UPLOAD DOCUMENT (TEMP STAGE)
    // =====================================================
    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam String tempId,
            @RequestParam String fileData,
            @RequestParam String fileName,
            @RequestParam String kycType
    ) {
        return service.uploadKycDocument(
                tempId,
                fileData,
                fileName,
                kycType
        );
    }
}