package com.los.administration.visibility.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Generalizes ManualShare (which is User-to-User only) to any record in
 * any downstream service - e.g. "share this specific LoanApplication with
 * this user" or "share every FI_TEAM record with this role". This is the
 * ad hoc-grant escape hatch sitting alongside the two rule-based checks in
 * RecordAccessService (role hierarchy, branch match) - most access should
 * be covered by those, this is for the exceptions.
 */
@Entity
@Table(name = "record_share")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_type", nullable = false)
    private String recordType; // e.g. "LOAN_APPLICATION"

    @Column(name = "record_id", nullable = false)
    private String recordId;

    /** Nullable - set when sharing with a specific user. */
    @Column(name = "shared_with_user_id")
    private String sharedWithUserId;

    /** Nullable - set when sharing with an entire role (and, via RoleHierarchyService, its subordinate roles). */
    @Column(name = "shared_with_role_id")
    private String sharedWithRoleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordAccessLevel accessLevel;

    private String reason;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = true;
    }
}
