package com.los.administration.auth.service;

import com.los.administration.auth.entity.PasswordResetTokenEntity;
import com.los.administration.auth.model.Credential;
import com.los.administration.auth.repository.CredentialRepository;
import com.los.administration.auth.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private static final int TOKEN_EXPIRY_MINUTES = 30;

    private final CredentialRepository credentialRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public String createPasswordSetupToken(String userId) {

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);

        PasswordResetTokenEntity entity =
                new PasswordResetTokenEntity(
                        userId,
                        tokenHash,
                        LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)
                );

        tokenRepository.save(entity);
        return rawToken; // send via email
    }

    @Transactional
    public void setPassword(String rawToken, String rawPassword) {

        PasswordResetTokenEntity token = tokenRepository.findAll()
                .stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        Credential credential = credentialRepository
                .findByUserId(token.getUserId())
                .orElseGet(() -> Credential.builder()
                        .userId(token.getUserId())
                        .active(true)
                        .passwordSet(false)
                        .build());

        credential.setPasswordHash(passwordEncoder.encode(rawPassword));
        credential.setPasswordSet(true);

        credentialRepository.save(credential);

        token.markUsed();
    }
}