package com.los.loanoriginatingsystem.report.controller;

import com.los.loanoriginatingsystem.report.dto.DashboardRequest;
import com.los.loanoriginatingsystem.report.dto.DashboardResponse;
import com.los.loanoriginatingsystem.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public List<DashboardResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DashboardResponse getById(
            @PathVariable String id
    ) {
        return service.getById(id);
    }

    // Full dashboard with every component's report freshly executed
    // — this is the endpoint the dashboard viewer page calls.
    @GetMapping("/{id}/data")
    public DashboardResponse getDashboardData(
            @PathVariable String id
    ) {
        return service.getDashboardData(id);
    }

    @PostMapping
    public DashboardResponse create(
            @RequestBody DashboardRequest request
    ) {
        return service.create(request, currentUser());
    }

    @PutMapping("/{id}")
    public DashboardResponse update(
            @PathVariable String id,
            @RequestBody DashboardRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    private String currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        return authentication.getName();
    }
}
