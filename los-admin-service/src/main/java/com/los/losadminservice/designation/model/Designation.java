package com.los.losadminservice.designation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Master data for a Designation (e.g. "Assistant Manager", "Senior Officer").
 * Designation is a display / HR-record concept distinct from Role: Role
 * drives hierarchy behaviour, Designation is the title printed on the
 * employee's record and documents. A Designation may optionally be scoped
 * to a Department, or left department-agnostic (null) if it is used
 * company-wide (e.g. "Trainee").
 */
@Entity
@Table(name = "designations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Designation {

    @Id
    @Column(name = "designation_id", length = 60)
    private String designationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "department_code", length = 40)
    private String departmentCode;

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
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
