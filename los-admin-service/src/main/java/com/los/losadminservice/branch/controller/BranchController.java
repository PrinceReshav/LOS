package com.los.losadminservice.branch.controller;

import com.los.losadminservice.branch.dto.*;
import com.los.losadminservice.branch.handler.BranchHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchHandler branchHandler;

    @PostMapping
    public BranchResponse create(
            @RequestBody BranchCreateRequest request
    ){
        return branchHandler.create(request);
    }

    @GetMapping("/{id}")
    public BranchResponse get(@PathVariable String id){
        return branchHandler.get(id);
    }

    @GetMapping("/search")
    public List<BranchResponse> search(
            @RequestParam String q
    ){
        return branchHandler.search(q);
    }

    @PutMapping("/{id}")
    public BranchResponse update(
            @PathVariable String id,
            @RequestBody BranchUpdateRequest request
    ){
        return branchHandler.update(id, request);
    }

    @PatchMapping("/{id}")
    public BranchResponse patch(
            @PathVariable String id,
            @RequestBody BranchUpdateRequest request
    ){
        return branchHandler.update(id, request);
    }
}