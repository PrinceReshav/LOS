package com.los.administration.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileRequest {

    @NotBlank
    private String profileId;

    @NotBlank
    private String profileName;

    private String description;

    private Boolean active;
}
