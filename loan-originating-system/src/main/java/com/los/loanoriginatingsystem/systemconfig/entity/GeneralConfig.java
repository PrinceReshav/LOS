package com.los.loanoriginatingsystem.systemconfig.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Generic key/value application settings, editable at runtime instead of
 * a redeploy - equivalent to Salesforce's General_Configuration__mdt
 * (used there for things like AML score thresholds, charge-label text,
 * default open dates). Values are stored as plain strings; callers parse
 * to whatever type they need (see SystemConfigService helper getters).
 */
@Entity
@Table(name = "general_config")
@Getter
@Setter
public class GeneralConfig {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String configKey;

    @Column(nullable = false, length = 2000)
    private String configValue;

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
