package com.los.loanoriginatingsystem.report.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
}
