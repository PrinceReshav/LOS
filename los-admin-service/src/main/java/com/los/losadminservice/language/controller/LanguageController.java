package com.los.losadminservice.language.controller;

import com.los.losadminservice.language.dto.LanguageRequest;
import com.los.losadminservice.language.dto.LanguageResponse;
import com.los.losadminservice.language.service.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    public LanguageResponse create(@Valid @RequestBody LanguageRequest request) {
        return languageService.create(request);
    }

    @GetMapping("/{code}")
    public LanguageResponse get(@PathVariable String code) {
        return languageService.get(code);
    }

    @GetMapping
    public List<LanguageResponse> getAll(
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return languageService.getAll(activeOnly);
    }
}
