package com.los.loanoriginatingsystem.notification.service;

import com.los.loanoriginatingsystem.notification.dto.NotificationRequest;
import com.los.loanoriginatingsystem.notification.client.NotificationClient;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final NotificationClient client;

    public void sendFailureAlert(TempLoanApplication temp, Exception e) {

        NotificationRequest req = new NotificationRequest(
                "SYSTEM_FAILURE_ALERT",
                List.of("admin@los.com"),
                Map.of(
                        "tempId", temp.getId(),
                        "leadId", String.valueOf(temp.getLeadId()),
                        "reason", String.valueOf(e.getMessage())
                )
        );

        client.send(req);
    }
}