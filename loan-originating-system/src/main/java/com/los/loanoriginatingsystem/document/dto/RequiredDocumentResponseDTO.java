package com.los.loanoriginatingsystem.document.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequiredDocumentResponseDTO {

    private List<String> applicantDocuments;
    private List<String> applicationDocuments;

    // useful for UI
    private List<String> alreadyUploaded;
}