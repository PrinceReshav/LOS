package com.los.loanoriginatingsystem.report.entity;

import com.los.loanoriginatingsystem.report.enums.AggregateFunction;
import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.ReportSourceObject;
import com.los.loanoriginatingsystem.report.enums.ReportType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "report_definition",
        indexes = @Index(name = "idx_report_definition_folder_id", columnList = "folderId")
)
@Data
public class ReportDefinition {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String folderId;

    @Enumerated(EnumType.STRING)
    private ReportSourceObject sourceObject;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    // Plain (non-aggregated) columns to display — used fully for
    // TABULAR reports, and alongside group/aggregate columns for
    // SUMMARY/MATRIX reports.
    @ElementCollection
    @CollectionTable(
            name = "report_selected_fields",
            joinColumns = @JoinColumn(name = "report_id")
    )
    @Column(name = "field_name")
    private List<String> selectedFields = new ArrayList<>();

    // Row-dimension grouping field (SUMMARY and MATRIX).
    private String groupByField1;

    // Column-dimension grouping field (MATRIX only).
    private String groupByField2;

    // The field an aggregate function is applied to, e.g.
    // "requestedAmount" with function SUM, or "id" with COUNT.
    private String aggregateField;

    @Enumerated(EnumType.STRING)
    private AggregateFunction aggregateFunction;

    private String sortField;

    // "ASC" or "DESC"
    private String sortDirection;

    @Enumerated(EnumType.STRING)
    private ChartType chartType;

    // Standard reports are seeded by the system and shown to every
    // user; they can be cloned but not edited/deleted in place.
    private Boolean isStandard;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
