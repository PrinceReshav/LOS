package com.los.loanoriginatingsystem.report.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private String id;
    private String name;
    private String description;
    private String folderId;
    private Boolean isStandard;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<DashboardComponentResponse> components;
}
