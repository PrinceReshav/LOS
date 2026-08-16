package com.los.loanoriginatingsystem.access.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecordAccessScopeResponse {
    private boolean seesAll;
    private List<String> visibleOwnerUserIds;
    private List<String> visibleBranchIds;
    private List<String> visibleRecordIds;
}
