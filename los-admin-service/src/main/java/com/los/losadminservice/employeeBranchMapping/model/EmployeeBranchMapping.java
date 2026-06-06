package com.los.losadminservice.employeeBranchMapping.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "employee_branch_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_branch",
                        columnNames = {"employee_id", "branch_id"}
                )
        },
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


    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}