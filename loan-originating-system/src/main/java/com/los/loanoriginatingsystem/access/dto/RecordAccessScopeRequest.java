package com.los.loanoriginatingsystem.access.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAccessScopeRequest {
    private String requestingUserId;
    private String recordType;
}
