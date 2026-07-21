package com.los.administration.profilepermission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePermissionMatrixResponse {

    private String profileId;
    private String profileName;
    private List<ProfilePermissionEntry> entries;
}
