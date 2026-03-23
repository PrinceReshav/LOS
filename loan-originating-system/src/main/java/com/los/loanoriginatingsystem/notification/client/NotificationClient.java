package com.los.loanoriginatingsystem.notification.client;

import com.los.loanoriginatingsystem.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${admin.service.url}")
    private String adminUrl;

    public void send(NotificationRequest request) {

        try {
            restTemplate.postForObject(
                    adminUrl + "/internal/notifications/send",
                    request,
                    Void.class
            );
        } catch (Exception e) {
            // ❌ NEVER break main flow
            System.out.println("Notification failed: " + e.getMessage());
        }
    }
}