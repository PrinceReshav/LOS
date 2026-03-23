package com.los.administration.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordSetupRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
