package com.los.administration.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String userId;
    private String username;
    private String role;
    private String token;
}
