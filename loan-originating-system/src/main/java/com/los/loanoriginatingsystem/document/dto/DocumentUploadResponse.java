package com.los.loanoriginatingsystem.document.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentUploadResponse {

    private String documentId;

    private String status;

    private String message;
}