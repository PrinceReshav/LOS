package com.los.losadminservice.employee.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="employee_id", nullable=false, unique=true)
    private String employeeId;

    @Column(name="user_id", nullable=false, unique=true)
    private String userId;

    private String fullName;

    private String email;

    private String mobile;

    private String roleId;
    private String roleName;


    private String profileId;
    private String profileName;

    /**
     * Department the employee belongs to (references Department.code).
     * Not part of the User/Employee gRPC contract - HR/Admin assigns this
     * within losAdminService itself, together with the reporting manager
     * and branch, once the employee record has arrived from the User
     * service via Kafka.
     */
    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "department_name")
    private String departmentName;

    /** References Designation.designationId. */
    @Column(name = "designation_id")
    private String designationId;

    @Column(name = "designation_name")
    private String designationName;

    // private String branchId;

    private String managerEmployeeId;

    /**
     * Deviation/commercial-approval level this employee is authorised to
     * approve at (0-5, matching loan-originating-system's
     * rules.enums.ApprovalLevel / UserRole levels). Distinct from
     * roleId/roleName above, which drive general RBAC - this is
     * specifically "how far up the deviation/commercial-approval chain can
     * this person sign off", independent of their org/profile role.
     * Null means the employee has no approval authority.
     */
    @Column(name = "approval_level")
    private Integer approvalLevel;

    /**
     * The approval role code this employee acts as when approving a
     * deviation or commercial-matrix escalation, e.g. "CBM", "CCM", "BH".
     * Must match one of loan-originating-system's rules.enums.UserRole
     * codes for cross-service approval routing to resolve correctly.
     */
    @Column(name = "approver_role_code")
    private String approverRoleCode;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        active = true;
    }

    @PreUpdate
    void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}