package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rule_engine")
@Data
public class RuleEngine {

    @Id
    private String id;

    private String name;

    private String objectApiName;     // LoanApplication, Document, etc

    private Boolean isDefault;

    private Boolean active;

    private String operator;       // AND / OR

    private String customLogic;    // "1 AND (2 OR 3)"

    private Integer deviationLevel;
}