package com.los.loanoriginatingsystem.insurance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Property insurance rate card, keyed by policy tenure. Percentage is
 * inclusive of GST, applied against the collateral/property value.
 * Equivalent to Salesforce's Property_Insurance_Rates__mdt.
 */
@Entity
@Table(name = "property_insurance_rate")
@Getter
@Setter
public class PropertyInsuranceRate {

    @Id
    private String id;

    @Column(name = "policy_tenure_months", nullable = false, unique = true)
    private Integer policyTenureMonths;

    @Column(name = "percentage_inc_gst", nullable = false, precision = 8, scale = 4)
    private BigDecimal percentageIncGst;

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
