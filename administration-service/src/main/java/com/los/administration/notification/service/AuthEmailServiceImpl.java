package com.los.administration.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEmailServiceImpl implements AuthEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordSetupEmail(
            String toEmail,
            String username,
            String setupLink
    ) {

        try {

            SimpleMailMessage mail =
                    new SimpleMailMessage();

            mail.setFrom(fromEmail);
            mail.setTo(toEmail);

            mail.setSubject(
                    "Set Up Your LOS Account"
            );

            mail.setText(
                    """
                    Hello %s,

                    Your LOS account has been created.

                    Click the link below to set your password:

                    %s

                    This link expires in 24 hours.

                    LOS Team
                    """
                            .formatted(
                                    username,
                                    setupLink
                            )
            );

            mailSender.send(mail);

            log.info(
                    "PASSWORD_SETUP_EMAIL_SENT | to={}",
                    toEmail
            );

        } catch (Exception ex) {

            log.error(
                    "PASSWORD_SETUP_EMAIL_FAILED | to={}",
                    toEmail,
                    ex
            );

            throw new RuntimeException(
                    "PASSWORD_SETUP_EMAIL_FAILED",
                    ex
            );
        }
    }

    @Override
    public void sendPasswordResetEmail(
            String toEmail,
            String username,
            String resetLink
    ) {

        try {

            SimpleMailMessage mail =
                    new SimpleMailMessage();

            mail.setFrom(fromEmail);
            mail.setTo(toEmail);

            mail.setSubject(
                    "Reset Your LOS Password"
            );

            mail.setText(
                    """
                    Hello %s,

                    Click the link below to reset your password:

                    %s

                    This link expires in 24 hours.

                    LOS Team
                    """
                            .formatted(
                                    username,
                                    resetLink
                            )
            );

            mailSender.send(mail);

            log.info(
                    "PASSWORD_RESET_EMAIL_SENT | to={}",
                    toEmail
            );

        } catch (Exception ex) {

            log.error(
                    "PASSWORD_RESET_EMAIL_FAILED | to={}",
                    toEmail,
                    ex
            );

            throw new RuntimeException(
                    "PASSWORD_RESET_EMAIL_FAILED",
                    ex
            );
        }
    }
}