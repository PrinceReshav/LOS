package com.los.administration.profilepermission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row of the permission matrix - a single module's Read/Create/Edit/
 * Delete/Approve flags for a profile. Used both for reading the current
 * matrix and for submitting updates to it.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePermissionEntry {

    private String permissionId;
    private String permissionCode;
    private String permissionName;
    private String moduleName;

    private Boolean canRead;
    private Boolean canCreate;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean canApprove;
}
