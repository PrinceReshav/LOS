package com.los.losadminservice.branch.dto;

import com.los.losadminservice.common.enums.BranchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponse {

    private String id;

    private String companyBranchId;

    private String branchName;

    private String address;

    private String pincode;

    private String district;

    private String state;

    private BranchType branchType;

    private Double latitude;

    private Double longitude;

    private Set<String> languageCodes;

    private Boolean active;
}
