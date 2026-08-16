package com.los.administration.user.model;

import com.los.administration.common.audit.BaseEntity;
import com.los.administration.license.model.UserLicenseType;
import com.los.administration.profile.model.Profile;
import com.los.administration.role.model.Role;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_role", columnList = "role_id"),
                @Index(name = "idx_profile", columnList = "profile_id"),
                @Index(name = "idx_employee", columnList = "employee_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "mobile")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Business Identity =====
    @NotBlank
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @NotBlank
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Size(min = 10, max = 15)
    @Column(nullable = false)
    private String mobile;

    @NotBlank
    @Column(nullable = false)
    private String alias;

    @NotBlank
    @Column(nullable = false)
    private String firstName;


    @Column
    private String middleName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Column(name = "employee_id", nullable = false)
    private String employeeId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // FIX: the organizational/hierarchy role (e.g. FIELD_OFFICER,
    // RELATIONSHIP_MANAGER, CEO) resolved from los-admin-service's role
    // catalog via OrgRoleClient. Stored as a plain string (not a JPA
    // relationship) because los-admin-service, not administration-service,
    // is the system of record for this data; administration-service only
    // keeps a validated copy of the id/name at the time the user was
    // created, for display and for re-sending on later updates.
    @Column(name = "org_role_id")
    private String orgRoleId;

    @Column(name = "org_role_name")
    private String orgRoleName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private UserLicenseType licenseType;

    // ===== Lifecycle =====
    @Column(nullable = false)
    private Boolean active;


    // ===== Audit =====
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdByUserId;

    @Column(name = "updated_by")
    private String updatedByUserId;




}