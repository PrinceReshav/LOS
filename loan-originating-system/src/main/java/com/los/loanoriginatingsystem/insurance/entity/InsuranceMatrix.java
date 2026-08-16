package com.los.loanoriginatingsystem.insurance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Life / credit-shield insurance rate card, keyed by applicant age band and
 * loan tenure. Equivalent to Salesforce's Insurance_Matrix__mdt, but a real
 * admin-editable table instead of deployable custom metadata.
 *
 * flatRate applies by default; flatReducedRate is a preferential rate the
 * underwriter/product may choose to apply (e.g. for a promotional scheme or
 * a lower-risk borrower segment) - kept as a separate column rather than a
 * discount percentage so the exact reduced number can be audited.
 */
@Entity
@Table(name = "insurance_matrix")
@Getter
@Setter
public class InsuranceMatrix {

    @Id
    private String id;

    /** Inclusive lower bound of the applicant age band this row applies to. */
    @Column(name = "min_age", nullable = false)
    private Integer minAge;

    /** Inclusive upper bound of the applicant age band this row applies to. */
    @Column(name = "max_age", nullable = false)
    private Integer maxAge;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "flat_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal flatRate;

    @Column(name = "flat_reduced_rate", precision = 10, scale = 4)
    private BigDecimal flatReducedRate;

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
