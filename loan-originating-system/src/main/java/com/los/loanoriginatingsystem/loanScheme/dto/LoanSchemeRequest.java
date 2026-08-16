package com.los.loanoriginatingsystem.loanScheme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanSchemeRequest {
    @NotBlank private String code;
    @NotBlank private String name;
    private String description;
}
