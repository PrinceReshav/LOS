package com.los.loanoriginatingsystem.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256/GCM encryption for secrets-at-rest (integration API keys/secrets,
 * and any other credential columns).
 *
 * Replaces the previous hardcoded AES/ECB implementation:
 *  - key is derived (SHA-256) from a configurable secret so any length of
 *    input key works, instead of a hardcoded 16-byte literal in source;
 *  - GCM mode with a random 12-byte IV per encryption call gives
 *    authenticated encryption and non-deterministic ciphertext (ECB leaked
 *    patterns for repeated/similar plaintext);
 *  - the IV is prefixed to the ciphertext so a single opaque Base64 string
 *    is still all that needs to be stored in a DB column.
 *
 * Configure via `los.security.encryption-key` (env: LOS_ENCRYPTION_KEY).
 * In production this MUST be overridden - the default is dev-only and is
 * intentionally called out as such below.
 */
@Component
public class EncryptionUtil {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKeySpec key;

    public EncryptionUtil(
            @Value("${los.security.encryption-key:CHANGE-ME-dev-only-encryption-key}") String rawKey
    ) {
        this.key = deriveKey(rawKey);
    }

    private static SecretKeySpec deriveKey(String rawKey) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha256.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES"); // 256-bit key
        } catch (Exception e) {
            throw new IllegalStateException("Unable to derive encryption key", e);
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(cipherText, 0, output, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encrypted) {
        if (encrypted == null) return null;
        try {
            byte[] input = Base64.getDecoder().decode(encrypted);

            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            System.arraycopy(input, 0, iv, 0, GCM_IV_LENGTH_BYTES);

            byte[] cipherText = new byte[input.length - GCM_IV_LENGTH_BYTES];
            System.arraycopy(input, GCM_IV_LENGTH_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /** Last 4 chars only, for admin UIs to confirm "which key is this" without exposing it. */
    public String mask(String plainOrNull) {
        if (plainOrNull == null || plainOrNull.isBlank()) return null;
        String trimmed = plainOrNull.trim();
        if (trimmed.length() <= 4) return "****";
        return "****" + trimmed.substring(trimmed.length() - 4);
    }
}
