package com.los.administration.visibility.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.security.annotation.RequiresPermission;
import com.los.administration.visibility.dto.SharingRuleRequest;
import com.los.administration.visibility.model.SharingRule;
import com.los.administration.visibility.service.SharingRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sharing-rules")
@RequiredArgsConstructor
public class SharingRuleController {

    private final SharingRuleService service;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "VISIBILITY", action = "CREATE")
    @PostMapping
    public ApiResponse<Void> createRule(@Valid @RequestBody SharingRuleRequest req) {

        service.createRule(req);
        return ApiResponse.success(null, "Sharing rule created");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "VISIBILITY", action = "READ")
    @GetMapping
    public ApiResponse<List<SharingRule>> getAll() {
        return ApiResponse.success(service.getAll(), "Sharing rules fetched successfully");
    }
}
