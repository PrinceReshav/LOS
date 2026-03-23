package com.los.losadminservice.branch.mapper;

import com.los.losadminservice.branch.dto.BranchResponse;
import com.los.losadminservice.branch.model.Branch;

public final class BranchMapper {

    private BranchMapper(){}

    public static BranchResponse toResponse(Branch branch){

        return BranchResponse.builder()
                .id(branch.getId())
                .companyBranchId(branch.getCompanyBranchId())
                .branchName(branch.getBranchName())
                .address(branch.getAddress())
                .pincode(branch.getPincode())
                .district(branch.getDistrict())
                .state(branch.getState())
                .active(branch.getActive())
                .build();
    }
}