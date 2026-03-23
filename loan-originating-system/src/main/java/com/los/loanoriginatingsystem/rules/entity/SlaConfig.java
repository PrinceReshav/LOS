package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sla_config")
@Data
public class SlaConfig {

    @Id
    private String id;

    private Integer deviationLevel;   // L1 / L2 / L3

    private Integer timeoutMinutes;  // SLA time

    private String action;
    // ESCALATE / AUTO_APPROVE / AUTO_REJECT

    private Integer nextLevel; // optional (for escalate)

    private Boolean active;
}
