package com.los.administration.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class PasswordSetupResponse {

    private String userId;
    private String message;
}
