package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "deviation")
@Data
public class Deviation {

    @Id
    private String id;

    private String ruleId;
    private String ruleName;

    private String applicationId;
    private String targetId;
    private String targetType;
    private String targetName;
    private Integer deviationLevel;


    private String status; // PENDING / APPROVED / REJECTED / RESOLVED

    private String comment;

    private Integer currentLevel; // L1 / L2 / L3
    private String finalStatus;  // APPROVED / REJECTED

    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

}