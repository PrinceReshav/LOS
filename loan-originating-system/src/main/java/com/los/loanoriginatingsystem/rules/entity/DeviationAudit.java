package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Table(name = "deviation_audit")
@Data
public class DeviationAudit {

    @Id
    private String id;

    private String deviationId;

    private String action; // APPROVED / REJECTED
    private Integer level;  // L1 / L2 / L3

    private String performedBy;
    private LocalDateTime performedAt;

    private String comment;

    @Column(name = "username")
    private String user;

    private LocalDateTime timestamp;

}