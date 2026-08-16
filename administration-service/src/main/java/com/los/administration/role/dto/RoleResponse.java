package com.los.administration.role.dto;

import com.los.administration.role.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Flat, cycle-free view of a Role. The entity itself has a bidirectional
 * parent/children relationship that must never be serialized directly -
 * doing so previously risked an infinite recursion (or a
 * LazyInitializationException) the moment a role tree had more than one
 * level, since Jackson would walk parent -> children -> parent forever.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private String roleId;
    private String roleName;
    private RoleType roleType;
    private String description;

    private String parentRoleId;
    private String parentRoleName;

    private Integer hierarchyLevel;
    private Boolean systemDefined;
    private Boolean active;

    private int childCount;
}
