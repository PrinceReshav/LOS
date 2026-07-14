package com.los.loanoriginatingsystem.document.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentSummaryDTO {

    private String id;

    private String documentType;

    private String fileName;

    private String contentType;

    private String status;

    private Boolean verified;
}