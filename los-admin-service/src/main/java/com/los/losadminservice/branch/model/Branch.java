package com.los.losadminservice.branch.model;

import com.los.losadminservice.common.enums.BranchType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name="branches",
        indexes = {
                @Index(name="idx_branch_name", columnList="branchName"),
                @Index(name="idx_company_branch_id", columnList="companyBranchId"),
                @Index(name="idx_branch_type", columnList="branch_type")
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

    /**
     * NORMAL (default) or HEAD_OFFICE. Head Office is what the Credit
     * department's hierarchy engine keys off of - see HierarchyRule.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "branch_type", nullable = false, length = 20)
    private BranchType branchType;

    /** Geo-coordinates of the branch, used for maps / nearest-branch features. */
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /**
     * Regional languages served by this branch. Document generation for any
     * employee mapped here should only ever be offered in these languages.
     * References Language.code (validated in BranchValidator, not via a
     * hard FK, so master data can evolve independently).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "branch_languages",
            joinColumns = @JoinColumn(name = "branch_id")
    )
    @Column(name = "language_code", length = 10)
    @Builder.Default
    private Set<String> languageCodes = new HashSet<>();

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        active = true;
        if (branchType == null) {
            branchType = BranchType.NORMAL;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}