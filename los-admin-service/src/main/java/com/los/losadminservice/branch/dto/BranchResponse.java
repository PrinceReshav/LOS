package com.los.losadminservice.branch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchResponse {

    private String id;

    private String companyBranchId;

    private String branchName;

    private String address;

    private String pincode;

    private String district;

    private String state;

    private Boolean active;
}