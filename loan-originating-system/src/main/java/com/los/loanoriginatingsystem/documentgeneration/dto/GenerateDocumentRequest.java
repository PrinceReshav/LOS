package com.los.loanoriginatingsystem.documentgeneration.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GenerateDocumentRequest {
    /** Extra key/value pairs to merge into the template alongside the standard loan-application model. */
    private Map<String, Object> extraData;
}
