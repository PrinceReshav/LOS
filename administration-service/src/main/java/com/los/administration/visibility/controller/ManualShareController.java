package com.los.administration.visibility.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.security.annotation.RequiresPermission;
import com.los.administration.visibility.dto.ManualShareRequest;
import com.los.administration.visibility.model.ManualShare;
import com.los.administration.visibility.service.ManualShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/manual-share")
@RequiredArgsConstructor
public class ManualShareController {

    private final ManualShareService service;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "VISIBILITY", action = "CREATE")
    @PostMapping
    public ApiResponse<Void> share(@Valid @RequestBody ManualShareRequest req) {

        service.share(req);
        return ApiResponse.success(null, "User shared successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "VISIBILITY", action = "READ")
    @GetMapping
    public ApiResponse<List<ManualShare>> getAll() {
        return ApiResponse.success(service.getAll(), "Manual shares fetched successfully");
    }
}
