package com.los.losadminservice.employeeBranchMapping.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "employee_branch_mapping",
        indexes = {

                @Index(
                        name = "idx_ebm_employee",
                        columnList = "employee_id"
                ),

                @Index(
                        name = "idx_ebm_branch",
                        columnList = "branch_id"
                ),

                @Index(
                        name = "idx_ebm_primary",
                        columnList = "primary_branch"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBranchMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "branch_id", nullable = false)
    private String branchId;

    /**
     * Marks this as the employee's primary branch. An employee can be
     * mapped to more than one branch (e.g. a Branch Credit Manager covering
     * a cluster), but exactly one active mapping is the primary one - this
     * is what "MyEmployee" / the Employee Details page shows as the main
     * branch.
     */
    @Column(name = "primary_branch", nullable = false)
    private Boolean primaryBranch;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "relieved_at")
    private LocalDateTime relievedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (assignedAt == null) {
            assignedAt = now;
        }

        if (active == null) {
            active = true;
        }

        if (primaryBranch == null) {
            primaryBranch = false;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}