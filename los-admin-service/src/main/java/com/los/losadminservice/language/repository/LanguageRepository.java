package com.los.losadminservice.language.repository;

import com.los.losadminservice.language.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, String> {

    boolean existsByCodeIgnoreCase(String code);

    List<Language> findByActiveTrue();
}
