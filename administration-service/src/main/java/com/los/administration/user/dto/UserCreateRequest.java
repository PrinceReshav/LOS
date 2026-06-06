package com.los.administration.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String mobile;

    @NotBlank
    private String alias;

    @NotBlank
    private String firstName;

    private String middleName; // optional

    @NotBlank
    private String lastName;

    @NotBlank
    private String employeeId;

    private String roleName;

    @NotBlank
    private String profileName;


    private String licenseType;
}
