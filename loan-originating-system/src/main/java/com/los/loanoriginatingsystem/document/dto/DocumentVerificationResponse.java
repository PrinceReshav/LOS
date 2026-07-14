package com.los.loanoriginatingsystem.document.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentVerificationResponse {

    private String documentId;

    private Boolean verified;

    private String message;

    private Object response;
}