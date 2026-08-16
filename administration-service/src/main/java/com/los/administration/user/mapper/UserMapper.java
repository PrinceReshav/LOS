package com.los.administration.user.mapper;


import com.los.administration.profile.model.Profile;
import com.los.administration.role.model.Role;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user, Role role, Profile profile) {

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .alias(user.getAlias())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .employeeId(user.getEmployeeId())
                .roleName(role.getRoleName())
                .orgRoleId(user.getOrgRoleId())
                .orgRoleName(user.getOrgRoleName())
                .profileName(profile.getProfileName())
                .licenseType(user.getLicenseType())
                .active(user.getActive())
                .build();
    }
}