package com.los.loanoriginatingsystem.documentgeneration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentTemplateRequest {

    @NotBlank private String code;
    @NotBlank private String name;
    private String description;
    private String applicableStage;

    @NotBlank private String htmlContent;
}
