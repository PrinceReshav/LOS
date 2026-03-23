package com.los.losadminservice.branch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name="branches",
        indexes = {
                @Index(name="idx_branch_name", columnList="branchName"),
                @Index(name="idx_company_branch_id", columnList="companyBranchId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @Column(length = 13)
    private String id;

    //To protect company data integrity we must enforce: UNIQUE(company_branch_id)
    @Column(nullable = false, unique = true, length = 4)
    private String companyBranchId;

    @Column(nullable = false)
    private String branchName;

    private String address;

    private String pincode;

    private String district;

    private String state;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        active = true;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}