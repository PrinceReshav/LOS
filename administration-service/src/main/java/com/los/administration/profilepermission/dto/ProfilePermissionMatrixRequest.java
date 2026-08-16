package com.los.administration.profilepermission.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfilePermissionMatrixRequest {

    private List<ProfilePermissionEntry> entries;
}
