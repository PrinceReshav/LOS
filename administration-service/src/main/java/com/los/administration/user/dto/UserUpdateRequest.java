package com.los.administration.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String email;
    private String mobile;
    private String alias;
    private String firstName;
    private String middleName;
    private String lastName;

    private String roleName;
    private String profileName;

    private Boolean active;
}