package com.los.administration.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${sms.api.url}")
    private String smsUrl;

    @Value("${sms.api.key}")
    private String apiKey;

    public void send(List<String> numbers, String message) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);

            Map<String, Object> payload = Map.of(
                    "numbers", numbers,
                    "message", message
            );

            HttpEntity<?> request = new HttpEntity<>(payload, headers);

            restTemplate.postForObject(smsUrl, request, String.class);

        } catch (Exception e) {
            log.error("SMS failed", e);
        }
    }
}