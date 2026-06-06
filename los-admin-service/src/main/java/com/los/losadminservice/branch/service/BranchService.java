package com.los.losadminservice.branch.service;

import com.los.losadminservice.branch.dto.BranchResponse;
import com.los.losadminservice.branch.model.Branch;
import com.los.losadminservice.branch.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    @Transactional
    public Branch create(Branch branch){

        if(branchRepository.existsByCompanyBranchId(branch.getCompanyBranchId())){
            throw new IllegalArgumentException("Branch already exists");
        }

        return branchRepository.save(branch);
    }

    @Transactional(readOnly = true)
    public Branch getById(String id){

        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    @Transactional(readOnly = true)
    public List<Branch> search(String keyword){

        return branchRepository
                .findByBranchNameContainingIgnoreCaseOrCompanyBranchIdContaining(keyword,keyword);
    }

    @Transactional
    public Branch update(Branch branch){

        return branchRepository.save(branch);
    }

    @Transactional(readOnly = true)
    public List<Branch> getAll(){
        return branchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllResponses() {

        return branchRepository.findAllResponses();
    }
}