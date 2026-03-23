package com.los.loanoriginatingsystem.rules.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BusinessRuleResult {

    private String ruleId;
    private String ruleName;
    private Integer deviationLevel;

    private List<String> failedCriteria; // ["Rule1", "Rule2"]
}