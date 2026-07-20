package com.los.losadminservice.hierarchyrule.model;

import com.los.losadminservice.common.enums.BranchType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single configurable "who-can-report-to-whom" rule.
 *
 * Example rows (seeded in data.sql, but equally manageable at runtime
 * through /admin/hierarchy-rules with zero code changes):
 *
 *   SALES  | RELATIONSHIP_MANAGER | TERRITORY_MANAGER        | (any branch)
 *   SALES  | RELATIONSHIP_MANAGER | ZONAL_BUSINESS_MANAGER   | (any branch)
 *   CREDIT | BRANCH_CREDIT_MANAGER| CLUSTER_CREDIT_MANAGER   | (any branch)
 *   CREDIT | CREDIT_ANALYST       | ZONAL_CREDIT_MANAGER     | HEAD_OFFICE
 *
 * A rule with branchType = null applies regardless of the employee's
 * branch. A rule with branchType = HEAD_OFFICE only applies when the
 * employee's active branch mapping points at a Head Office branch, and
 * (by convention enforced in the validator) overrides/replaces the
 * branch-agnostic rules for that (department, fromRole) pair - this is
 * exactly how the Credit "Head Office reports straight to Zonal Credit
 * Manager" carve-out is expressed without a single hardcoded if-statement
 * for Credit specifically.
 *
 * fromRoleId/toRoleId reference Role.roleId (not enforced via a hard FK,
 * so rules can be authored slightly ahead of roles during setup, but the
 * service layer validates both exist before saving).
 */
@Entity
@Table(
        name = "hierarchy_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hierarchy_rule",
                        columnNames = {"department_code", "from_role_id", "to_role_id", "branch_type"}
                )
        },
        indexes = {
                @Index(name = "idx_hr_dept_from", columnList = "department_code,from_role_id"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_code", nullable = false, length = 40)
    private String departmentCode;

    @Column(name = "from_role_id", nullable = false, length = 60)
    private String fromRoleId;

    @Column(name = "to_role_id", nullable = false, length = 60)
    private String toRoleId;

    /**
     * Null (stored as "ANY") means the rule applies regardless of branch
     * type. HEAD_OFFICE means the rule only applies - and takes priority
     * over ANY rules - when the employee's branch is the Head Office.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "branch_type")
    private BranchType branchType;

    @Column(name = "priority", nullable = false)
    private Integer priority;

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
        if (priority == null) priority = 0;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
