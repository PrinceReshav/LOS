package com.los.administration.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private String profileId;
    private String profileName;
    private String description;
    private Boolean systemDefined;
    private Boolean active;
}
