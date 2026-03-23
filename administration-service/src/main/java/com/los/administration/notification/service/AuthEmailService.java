package com.los.administration.notification.service;

public interface AuthEmailService {

    void sendPasswordSetupEmail(
            String email,
            String username,
            String setupLink
    );
    void sendPasswordResetEmail(
            String toEmail,
            String username,
            String resetLink
    );
}
