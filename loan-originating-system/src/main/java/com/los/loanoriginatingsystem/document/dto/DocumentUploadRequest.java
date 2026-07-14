package com.los.loanoriginatingsystem.document.dto;

import lombok.Data;

@Data
public class DocumentUploadRequest {

    private String tempId;

    private String documentType;

    private String fileName;

    private String fileData;
}