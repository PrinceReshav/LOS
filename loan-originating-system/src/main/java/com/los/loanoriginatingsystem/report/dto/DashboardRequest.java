package com.los.loanoriginatingsystem.report.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardRequest {

    private String name;
    private String description;
    private String folderId;
    private List<String> filterFields = new ArrayList<>();
    private List<DashboardComponentRequest> components = new ArrayList<>();
}
