package com.los.loanoriginatingsystem.rules.model;

import lombok.Data;

import java.util.List;

@Data
public class DeviationResponseDTO {

    private String targetId;
    private String targetName;
    private String objectName;

    private List<DeviationItem> deviations;

    @Data
    public static class DeviationItem {
        private String deviationId;
        private String ruleName;
        private String level;
        private String status;
    }
}