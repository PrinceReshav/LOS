package com.los.losadminservice.role.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Master data for a Role (Field Officer, Relationship Manager, Branch Credit
 * Manager, CEO, ...).
 *
 * A Role belongs to exactly one Department, and carries the behavioural
 * flags that the hierarchy engine needs. This is what lets the engine stay
 * generic: none of these flags are hardcoded in Java, they are configured
 * once per role and read at validation time.
 *
 *  - isTopLevel                : role sits at the very top of the org chart
 *                                 (CEO, MD, CTO, CHRO, CFO, Risk Head, Audit
 *                                 Head, Credit Head, ...). Such an employee
 *                                 can never be assigned a manager and can see
 *                                 every branch / every case.
 *  - singleBranchOnly           : an employee holding this role may only ever
 *                                 be mapped to ONE active branch at a time
 *                                 (e.g. Relationship Officer / Field Officer /
 *                                 Relationship Manager).
 *  - requiresManagerBranchAlign : an employee holding this role may be mapped
 *                                 to MULTIPLE branches, but only to a branch
 *                                 where their reporting manager is also mapped
 *                                 (e.g. Branch Credit Manager).
 *  - maxPerBranch                : maximum number of ACTIVE employees holding
 *                                 this role that may be mapped to the same
 *                                 branch at once. Null = unlimited.
 *  - maxDirectReports            : maximum number of ACTIVE direct reports an
 *                                 employee holding this role may manage.
 *                                 Null = unlimited.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @Column(name = "role_id", length = 60)
    private String roleId;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "department_code", nullable = false, length = 40)
    private String departmentCode;

    @Column(name = "is_top_level", nullable = false)
    private Boolean isTopLevel;

    @Column(name = "single_branch_only", nullable = false)
    private Boolean singleBranchOnly;

    @Column(name = "requires_manager_branch_align", nullable = false)
    private Boolean requiresManagerBranchAlign;

    @Column(name = "max_per_branch")
    private Integer maxPerBranch;

    @Column(name = "max_direct_reports")
    private Integer maxDirectReports;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (active == null) active = true;
        if (isTopLevel == null) isTopLevel = false;
        if (singleBranchOnly == null) singleBranchOnly = false;
        if (requiresManagerBranchAlign == null) requiresManagerBranchAlign = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
