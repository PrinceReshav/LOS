package com.los.losadminservice.branch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchCreateRequest {

    @NotBlank
    private String companyBranchId;

    @NotBlank
    private String branchName;

    private String address;

    private String pincode;

    private String district;

    private String state;
}