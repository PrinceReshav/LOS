package com.los.administration.profilepermission.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "profile_permissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "profile_id",
                                "permission_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private String profileId;

    @Column(name = "permission_id", nullable = false)
    private String permissionId;

    private Boolean canRead;

    private Boolean canCreate;

    private Boolean canEdit;

    private Boolean canDelete;

    private Boolean canApprove;
}