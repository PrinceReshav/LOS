package com.los.losadminservice.language.service;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.common.exception.ResourceNotFoundException;
import com.los.losadminservice.language.dto.LanguageRequest;
import com.los.losadminservice.language.dto.LanguageResponse;
import com.los.losadminservice.language.model.Language;
import com.los.losadminservice.language.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Transactional
    public LanguageResponse create(LanguageRequest request) {

        String code = request.getCode().trim().toLowerCase();

        if (languageRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessRuleViolationException("Language already exists: " + code);
        }

        Language language = Language.builder()
                .code(code)
                .name(request.getName())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(languageRepository.save(language));
    }

    @Transactional(readOnly = true)
    public LanguageResponse get(String code) {
        return toResponse(getEntity(code));
    }

    @Transactional(readOnly = true)
    public Language getEntity(String code) {

        return languageRepository.findById(code.toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Language not found: " + code)
                );
    }

    @Transactional(readOnly = true)
    public List<LanguageResponse> getAll(boolean activeOnly) {

        List<Language> languages = activeOnly
                ? languageRepository.findByActiveTrue()
                : languageRepository.findAll();

        return languages.stream().map(this::toResponse).toList();
    }

    /**
     * Validates that every language code supplied for a Branch actually
     * exists in the master table. Called by the branch validator.
     */
    @Transactional(readOnly = true)
    public void validateCodesExist(Set<String> codes) {

        if (codes == null) return;

        for (String code : codes) {
            if (!languageRepository.existsByCodeIgnoreCase(code)) {
                throw new BusinessRuleViolationException(
                        "Unknown regional language code: " + code
                );
            }
        }
    }

    private LanguageResponse toResponse(Language language) {

        return LanguageResponse.builder()
                .code(language.getCode())
                .name(language.getName())
                .active(language.getActive())
                .build();
    }
}
