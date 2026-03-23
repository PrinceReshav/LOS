package com.los.losadminservice.branch.handler;

import com.los.losadminservice.branch.dto.*;
import com.los.losadminservice.branch.mapper.BranchMapper;
import com.los.losadminservice.branch.model.Branch;
import com.los.losadminservice.branch.service.BranchService;
import com.los.losadminservice.branch.util.BranchIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchService branchService;

    public BranchResponse create(BranchCreateRequest request){

        Branch branch = Branch.builder()
                .id(BranchIdGenerator.generate(request.getCompanyBranchId()))
                .companyBranchId(request.getCompanyBranchId())
                .branchName(request.getBranchName())
                .address(request.getAddress())
                .pincode(request.getPincode())
                .district(request.getDistrict())
                .state(request.getState())
                .build();

        Branch saved = branchService.create(branch);

        return BranchMapper.toResponse(saved);
    }

    public BranchResponse get(String id){

        return BranchMapper.toResponse(
                branchService.getById(id)
        );
    }

    public List<BranchResponse> search(String keyword){

        return branchService.search(keyword)
                .stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BranchResponse update(String id, BranchUpdateRequest req){

        Branch branch = branchService.getById(id);

        if(req.getBranchName()!=null)
            branch.setBranchName(req.getBranchName());

        if(req.getAddress()!=null)
            branch.setAddress(req.getAddress());

        if(req.getPincode()!=null)
            branch.setPincode(req.getPincode());

        if(req.getDistrict()!=null)
            branch.setDistrict(req.getDistrict());

        if(req.getState()!=null)
            branch.setState(req.getState());

        if(req.getActive()!=null)
            branch.setActive(req.getActive());

        return BranchMapper.toResponse(
                branchService.update(branch)
        );
    }
}