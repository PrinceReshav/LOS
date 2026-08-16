package com.los.losadminservice.branch.dto;

import com.los.losadminservice.common.enums.BranchType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

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

    /** Defaults to NORMAL when omitted. */
    private BranchType branchType;

    private Double latitude;

    private Double longitude;

    /** Language codes (must exist in the Language master table). */
    private Set<String> languageCodes;
}
