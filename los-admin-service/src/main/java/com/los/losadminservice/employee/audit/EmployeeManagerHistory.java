package com.los.losadminservice.employee.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit trail of every reporting-manager change for an employee.
 * Written automatically whenever a manager is assigned, changed, or
 * removed (including as a side-effect of a promotion), so "who reported to
 * whom, and since when" can always be reconstructed - required for HR
 * audits and for safely handling promotions without losing history.
 */
@Entity
@Table(
        name = "employee_manager_history",
        indexes = {
                @Index(name = "idx_emh_employee", columnList = "employee_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeManagerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "old_manager_employee_id")
    private String oldManagerEmployeeId;

    @Column(name = "new_manager_employee_id")
    private String newManagerEmployeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private ManagerChangeType changeType;

    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
