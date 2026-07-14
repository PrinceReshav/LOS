package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.dto.DocumentUploadRequest;
import com.los.loanoriginatingsystem.document.dto.DocumentUploadResponse;
import com.los.loanoriginatingsystem.document.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentUploadController {

    private final DocumentUploadService service;

    @PostMapping("/upload")
    public DocumentUploadResponse upload(
            @RequestBody DocumentUploadRequest request
    ) {
        return service.upload(request);
    }
}
