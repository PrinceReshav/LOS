package com.los.loanoriginatingsystem.notification.service;

import com.los.loanoriginatingsystem.notification.dto.NotificationRequest;
import com.los.loanoriginatingsystem.notification.client.NotificationClient;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final NotificationClient client;

    public void sendFailureAlert(TempLoanApplication temp, Exception e) {

        NotificationRequest req = new NotificationRequest();

        req.setType("EMAIL");
        req.setRecipients(List.of("admin@los.com"));

        req.setSubject("🚨 Loan Processing FAILED_FINAL");

        req.setBody(
                "TempId: " + temp.getId() + "\n" +
                        "LeadId: " + temp.getLeadId() + "\n" +
                        "Reason: " + e.getMessage()
        );

        client.send(req);
    }
}