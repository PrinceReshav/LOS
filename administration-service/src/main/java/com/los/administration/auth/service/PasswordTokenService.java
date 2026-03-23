package com.los.administration.auth.service;

import com.los.administration.auth.entity.PasswordResetTokenEntity;
import com.los.administration.auth.repository.PasswordResetTokenRepository;
import com.los.administration.common.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordTokenService {

    private static final int EXPIRY_HOURS = 24;

    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository repository;

    public String generateToken(String userId, String email) {

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);

        PasswordResetTokenEntity entity =
                new PasswordResetTokenEntity(
                        userId,
                        tokenHash,
                        LocalDateTime.now().plusHours(EXPIRY_HOURS)
                );

        repository.save(entity);

        return rawToken;
    }

    public PasswordResetTokenEntity validateToken(String rawToken) {

        return repository.findAll().stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() ->
                        new InvalidTokenException("Invalid or expired token"));
    }

}
