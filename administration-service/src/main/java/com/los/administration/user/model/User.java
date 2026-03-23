package com.los.administration.user.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
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
public class User {

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

    @NotBlank
    @Column(name = "role_id", nullable = false)
    private String roleId;

    @NotBlank
    @Column(name = "profile_id", nullable = false)
    private String profileId;


    // ===== Lifecycle =====
    @Column(nullable = false)
    private Boolean active;


    // ===== Audit =====
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdByUserId;

    @Column(name = "updated_by")
    private String updatedByUserId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}



/*
*
*     // ===== Password =====
    @Column(name = "password", nullable = false)
    private String password;
*/