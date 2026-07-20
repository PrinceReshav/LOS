package com.los.loanoriginatingsystem.report.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dashboard")
@Data
public class Dashboard {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String folderId;

    private Boolean isStandard;

    private String createdBy;

    private LocalDateTime createdAt;

    // Fields exposed as dashboard-wide filter controls. At render
    // time, a filter only applies to the components whose underlying
    // report's source object actually has that field — mirroring
    // how Salesforce dashboard filters skip incompatible components.
    @ElementCollection
    @CollectionTable(
            name = "dashboard_filter_field",
            joinColumns = @JoinColumn(name = "dashboard_id")
    )
    @Column(name = "field_name")
    private List<String> dashboardFilterFields = new java.util.ArrayList<>();
}
