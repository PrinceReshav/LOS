package com.los.loanoriginatingsystem.report.controller;

import com.los.loanoriginatingsystem.report.entity.ReportFolder;
import com.los.loanoriginatingsystem.report.service.ReportFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/folders")
@RequiredArgsConstructor
public class ReportFolderController {

    private final ReportFolderService service;

    @GetMapping
    public List<ReportFolder> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ReportFolder create(
            @RequestBody Map<String, String> request
    ) {
        return service.create(
                request.get("name"),
                request.get("description")
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
