package com.los.administration.visibility.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.visibility.dto.ManualShareRequest;
import com.los.administration.visibility.service.ManualShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/manual-share")
@RequiredArgsConstructor
public class ManualShareController {

    private final ManualShareService service;

    @PostMapping
    public ApiResponse<Void> share(
            @Valid @RequestBody ManualShareRequest req) {

        service.share(req);
        return ApiResponse.success(null, "User shared successfully");
    }
}