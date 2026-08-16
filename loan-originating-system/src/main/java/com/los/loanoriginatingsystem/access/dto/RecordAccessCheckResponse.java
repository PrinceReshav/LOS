package com.los.loanoriginatingsystem.access.dto;

import lombok.Data;

@Data
public class RecordAccessCheckResponse {
    private boolean allowed;
    private String reason;
}
