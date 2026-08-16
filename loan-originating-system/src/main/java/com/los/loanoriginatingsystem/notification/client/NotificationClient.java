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

    // FIX: this previously used admin.service.url, which points at
    // los-admin-service (port 8081, employee/branch/org-role data) - but
    // /internal/notifications/send only exists in administration-service
    // (port 8080, see NotificationController). Every call silently 404'd
    // and was swallowed by the catch-all below, so no notification was
    // ever actually delivered.
    @Value("${administration.service.url}")
    private String administrationServiceUrl;

    public void send(NotificationRequest request) {

        try {
            restTemplate.postForObject(
                    administrationServiceUrl + "/internal/notifications/send",
                    request,
                    Void.class
            );
        } catch (Exception e) {
            // ❌ NEVER break main flow
            System.out.println("Notification failed: " + e.getMessage());
        }
    }
}