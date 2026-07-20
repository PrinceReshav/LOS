package com.los.losadminservice.language.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LanguageRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private Boolean active;
}
