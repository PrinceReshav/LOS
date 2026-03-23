package com.los.losadminservice.branch.dto;

import lombok.Data;

@Data
public class BranchUpdateRequest {

    private String branchName;
    private String address;
    private String pincode;
    private String district;
    private String state;
    private Boolean active;
}