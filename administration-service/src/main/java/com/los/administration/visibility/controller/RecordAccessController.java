package com.los.administration.visibility.controller;

import com.los.administration.visibility.dto.RecordAccessCheckRequest;
import com.los.administration.visibility.dto.RecordAccessCheckResponse;
import com.los.administration.visibility.dto.RecordAccessScopeRequest;
import com.los.administration.visibility.dto.RecordAccessScopeResponse;
import com.los.administration.visibility.service.RecordAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Called by other services (e.g. loan-originating-system) to authorize
 * access to a specific record, or to fetch a user's visibility scope for
 * filtering a list query. Internal service-to-service endpoint - not
 * intended to be called directly by a browser client.
 */
@RestController
@RequestMapping("/internal/record-access")
@RequiredArgsConstructor
public class RecordAccessController {

    private final RecordAccessService service;

    @PostMapping("/check")
    public RecordAccessCheckResponse check(@Valid @RequestBody RecordAccessCheckRequest request) {
        return service.check(request);
    }

    @PostMapping("/scope")
    public RecordAccessScopeResponse scope(@Valid @RequestBody RecordAccessScopeRequest request) {
        return service.scope(request);
    }
}
