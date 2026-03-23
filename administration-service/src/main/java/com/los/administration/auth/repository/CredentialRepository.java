package com.los.administration.auth.repository;

import com.los.administration.auth.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository
        extends JpaRepository<Credential, Long> {

    Optional<Credential> findByUserId(String userId);
}