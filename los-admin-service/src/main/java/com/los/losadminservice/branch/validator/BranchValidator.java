package com.los.losadminservice.branch.validator;

import com.los.losadminservice.common.exception.BusinessRuleViolationException;
import com.los.losadminservice.language.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BranchValidator {

    private final LanguageService languageService;

    /**
     * A branch must serve at least one regional language, since document
     * generation for every employee mapped to it depends on this list.
     */
    public void validateLanguages(Set<String> languageCodes) {

        if (languageCodes == null || languageCodes.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "At least one regional language must be configured for a branch"
            );
        }

        languageService.validateCodesExist(languageCodes);
    }
}
