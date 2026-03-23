package com.los.administration.notification.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackAlertService {

    private final RestTemplate restTemplate;
    private final AlertProperties properties;

    public void send(String message) {

        if (!properties.getSlack().isEnabled()) return;

        try {
            restTemplate.postForObject(
                    properties.getSlack().getWebhookUrl(),
                    new HttpEntity<>(Map.of("text", message)),
                    String.class
            );
        } catch (Exception e) {
            log.error("Slack alert failed", e);
        }
    }
}