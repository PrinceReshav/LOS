package com.los.administration.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    public void send(List<String> to, String subject, String body) {

        try {
            // Replace with actual provider (SMTP / SES / SendGrid)
            log.info("Sending EMAIL to {} subject={}", to, subject);

        } catch (Exception e) {
            log.error("Email failed", e);
        }
    }
}