package com.los.loanoriginatingsystem.report.enums;

public enum ReportType {

    // Flat list of rows, no grouping.
    TABULAR,

    // Grouped by one field, with aggregate(s) per group.
    SUMMARY,

    // Grouped by two fields (row + column dimension), with
    // aggregate(s) per cell — the frontend pivots the returned
    // rows into a grid.
    MATRIX
}
