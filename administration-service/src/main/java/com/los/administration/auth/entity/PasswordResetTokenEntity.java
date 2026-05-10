package com.los.administration.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_reset_token",
        indexes = {
                @Index(name = "idx_prt_user_id", columnList = "user_id"),
                @Index(name = "idx_prt_token_hash", columnList = "token_hash", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "token_lookup", nullable = false)
    private String tokenLookup;

    public PasswordResetTokenEntity(
            String userId,
            String tokenHash,
            String tokenLookup,
            LocalDateTime expiresAt
    ) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.tokenLookup = tokenLookup;
        this.expiresAt = expiresAt;
    }

    public void markUsed() {
        this.used = true;
    }

}