package com.los.administration.visibility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManualShareRequest {

    @NotBlank
    private String ownerUserId;

    @NotBlank
    private String sharedWithUserId;
}