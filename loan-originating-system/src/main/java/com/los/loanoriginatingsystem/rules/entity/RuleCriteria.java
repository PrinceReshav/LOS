package com.los.loanoriginatingsystem.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rule_criteria")
@Data
public class RuleCriteria {

    @Id
    private String id;
    private String ruleEngineId;
    private String fieldName;
    private String operator;
    @Column(name = "criteria_value")
    private String value;
    private Boolean fieldCompare;
    private Integer sequence;
}