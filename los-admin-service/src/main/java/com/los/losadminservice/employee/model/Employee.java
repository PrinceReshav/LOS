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

    private String designation;

    private String roleId;

    private String profileId;

    private String branchId;

    private String managerEmployeeId;

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