package com.los.loanoriginatingsystem.documentgeneration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDocumentResponse {
    private String documentId;
    private String documentType;
    private String fileName;
    private String previewUrl;
}
