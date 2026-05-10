package com.los.administration.auth.service;

import com.los.administration.auth.entity.PasswordResetTokenEntity;
import com.los.administration.auth.repository.PasswordResetTokenRepository;
import com.los.administration.common.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
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
        String lookup = DigestUtils.sha256Hex(rawToken);


        PasswordResetTokenEntity entity =
                new PasswordResetTokenEntity(
                        userId,
                        tokenHash,
                        lookup,
                        LocalDateTime.now().plusHours(EXPIRY_HOURS)
                );

        repository.save(entity);

        return rawToken;
    }

    public PasswordResetTokenEntity validateToken(String rawToken) {

        String lookup = DigestUtils.sha256Hex(rawToken);

        PasswordResetTokenEntity token =
                repository.findByTokenLookupAndUsedFalseAndExpiresAtAfter(
                        lookup,
                        LocalDateTime.now()
                ).orElseThrow(() ->
                        new InvalidTokenException("Invalid or expired token"));

        // 🔒 IMPORTANT: Always keep this
        if (!passwordEncoder.matches(rawToken, token.getTokenHash())) {
            throw new InvalidTokenException("Invalid token");
        }

        return token;
    }

}
