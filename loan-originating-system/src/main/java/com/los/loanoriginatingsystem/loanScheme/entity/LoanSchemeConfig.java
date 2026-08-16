package com.los.loanoriginatingsystem.loanScheme.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Admin-manageable Loan Scheme master (e.g. "ITR Scheme", "ABB Scheme",
 * "GST Scheme"). LoanProduct still references a scheme by its `code`
 * (kept in sync with com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanScheme
 * for backward compatibility with existing product records), but the
 * scheme itself - name, description, and whether it is currently offered -
 * is now editable/activatable at runtime instead of being a fixed enum.
 *
 * New schemes can be added here with a fresh `code` even before a matching
 * enum constant exists; existing callers that still switch on the enum
 * will simply not recognise the new code until the enum is extended, but
 * every new admin-facing flow (LoanProductService validation, Commercial
 * Matrix, reporting) should read against this table, not the enum.
 */
@Entity
@Table(name = "loan_scheme_config")
@Getter
@Setter
public class LoanSchemeConfig {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

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
