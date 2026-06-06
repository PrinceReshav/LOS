package com.los.administration.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private String userId;
    private String username;

    private String firstName;
    private String lastName;

    private String email;
    private String mobile;

    private String employeeId;

    private String role;
    private String profile;

    private Boolean active;
}