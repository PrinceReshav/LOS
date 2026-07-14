package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.document.dto.DocumentUploadRequest;
import com.los.loanoriginatingsystem.document.dto.DocumentUploadResponse;

public interface DocumentUploadService {

    DocumentUploadResponse upload(
            DocumentUploadRequest request
    );
}