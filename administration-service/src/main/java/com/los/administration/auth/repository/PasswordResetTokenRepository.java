package com.los.administration.auth.repository;

import com.los.administration.auth.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity>
    findByUserIdAndUsedFalse(String userId);

    Optional<PasswordResetTokenEntity>
    findByTokenHashAndUsedFalse(String tokenHash);

    Optional<PasswordResetTokenEntity> findByTokenLookupAndUsedFalse(String tokenLookup);

    Optional<PasswordResetTokenEntity>
    findByTokenLookupAndUsedFalseAndExpiresAtAfter(
            String tokenLookup,
            LocalDateTime now
    );
}
