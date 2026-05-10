package com.los.administration.visibility.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.visibility.dto.SharingRuleRequest;
import com.los.administration.visibility.service.SharingRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sharing-rules")
@RequiredArgsConstructor
public class SharingRuleController {

    private final SharingRuleService service;

    @PostMapping
    public ApiResponse<Void> createRule(
            @Valid @RequestBody SharingRuleRequest req) {

        service.createRule(req);
        return ApiResponse.success(null, "Sharing rule created");
    }
}