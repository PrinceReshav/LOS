package com.los.loanoriginatingsystem.access.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAccessCheckRequest {
    private String requestingUserId;
    private String recordType;
    private String recordId;
    private String ownerUserId;
    private String branchId;
    private RecordAccessLevel requiredAccess;
}
