package com.los.administration.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String userId;

    private String username;
    private String email;
    private String mobile;

    private String alias;
    private String firstName;
    private String middleName;
    private String lastName;

    private String employeeId;

    private String roleName;
    private String profileName;

    private boolean active;
}
