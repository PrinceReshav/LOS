package com.los.administration.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "field_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profileId;
    private String objectName;
    private String fieldName;

    private Boolean canRead;
    private Boolean canWrite;
    private Boolean masked;
}