package com.los.loanoriginatingsystem.report.entity;

import com.los.loanoriginatingsystem.report.enums.FilterOperator;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "report_filter_criteria")
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
    private String value;

    // Second value, only used for the BETWEEN operator.
    private String value2;
}
