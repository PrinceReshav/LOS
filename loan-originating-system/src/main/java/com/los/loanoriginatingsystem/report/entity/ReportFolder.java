package com.los.loanoriginatingsystem.report.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_folder")
@Data
public class ReportFolder {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    // System folders (e.g. "Standard Reports") can't be deleted
    // by end users, mirroring Salesforce's protected folders.
    private Boolean isSystemFolder;

    private String createdBy;

    private LocalDateTime createdAt;
}
