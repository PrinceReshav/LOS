package com.los.loanoriginatingsystem.document.dto;

import lombok.Data;

import java.util.List;

@Data
public class DocumentChecklistResponse {

    private List<String> requiredDocuments;

    private List<String> uploadedDocuments;

    // Required documents split by category, plus the list already
    // uploaded — used by the DocumentChecklistPanel on the frontend.
    private List<String> applicantDocuments;

    private List<String> applicationDocuments;

    private List<String> alreadyUploaded;
}