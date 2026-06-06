package com.los.administration.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String userId;
    private String username;

    private String firstName;
    private String lastName;

    private String role;
    private String profile;

    private String token;
}
