package com.los.losadminservice.branch.dto;

import com.los.losadminservice.common.enums.BranchType;
import lombok.Data;

import java.util.Set;

@Data
public class BranchUpdateRequest {

    private String branchName;
    private String address;
    private String pincode;
    private String district;
    private String state;
    private Boolean active;
    private BranchType branchType;
    private Double latitude;
    private Double longitude;
    private Set<String> languageCodes;
}
