package com.los.administration.visibility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SharingRuleRequest {

    @NotBlank
    private String fromRoleName;

    @NotBlank
    private String toRoleName;
}