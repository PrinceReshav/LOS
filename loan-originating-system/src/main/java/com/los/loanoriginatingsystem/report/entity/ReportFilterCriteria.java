package com.los.loanoriginatingsystem.report.entity;

import com.los.loanoriginatingsystem.report.enums.FilterOperator;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "report_filter_criteria",
        indexes = @Index(name = "idx_filter_criteria_report_id", columnList = "reportId")
)
@Data
public class ReportFilterCriteria {

    @Id
    private String id;

    private String reportId;

    private String fieldName;

    @Enumerated(EnumType.STRING)
    private FilterOperator operator;

    // Raw value as typed by the user; parsed into the field's actual
    // type at execution time by the query engine.
    // Explicitly named — "VALUE" is a reserved SQL keyword in H2 and
    // would otherwise generate an unquoted, unparseable column
    // reference in every query against this table.
    @Column(name = "filter_value")
    private String value;

    // Second value, only used for the BETWEEN operator.
    @Column(name = "filter_value2")
    private String value2;
}
