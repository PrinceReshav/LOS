package com.los.loanoriginatingsystem.systemconfig.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * State-wise stamp duty rate used when generating loan agreement /
 * mortgage documents. Equivalent to Salesforce's State_Wise_Stamp_Duty__mdt.
 */
@Entity
@Table(name = "stamp_duty_config")
@Getter
@Setter
public class StampDutyConfig {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String stateCode;

    @Column(nullable = false)
    private String stateName;

    /** Percentage of loan/property value charged as stamp duty. */
    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal stampDutyPercent;

    /** Flat fee in addition to (or instead of) the percentage, if applicable. */
    @Column(precision = 12, scale = 2)
    private BigDecimal flatFee;

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
