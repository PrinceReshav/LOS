package com.los.administration.security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "securityProfile_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityProfilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profileId;

    private Long permissionId;

    private Boolean allowed;
}