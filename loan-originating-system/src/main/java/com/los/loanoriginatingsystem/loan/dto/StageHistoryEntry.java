package com.los.loanoriginatingsystem.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageHistoryEntry {
    private String fromStage;
    private String toStage;
    private String performedBy;
    private String remarks;
    private LocalDateTime occurredAt;
}
