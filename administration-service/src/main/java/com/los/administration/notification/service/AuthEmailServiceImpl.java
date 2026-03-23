package com.los.administration.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthEmailServiceImpl implements AuthEmailService {

    @Override
    public void sendPasswordSetupEmail(
            String toEmail,
            String username,
            String setupLink
    ) {
        // TEMP: log-based (SMTP later)
        log.info(
                "EMAIL_SENT | type=PASSWORD_SETUP | to={} | username={} | link={}",
                toEmail, username, setupLink
        );
    }

    @Override
    public void sendPasswordResetEmail(
            String toEmail,
            String username,
            String resetLink
    ) {
        // TEMP: log-based (SMTP later)
        log.info(
                "EMAIL_SENT | type=PASSWORD_RESET | to={} | username={} | link={}",
                toEmail, username, resetLink
        );
    }
}
