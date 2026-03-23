package com.los.loanoriginatingsystem.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private String type; // EMAIL / SMS

    private List<String> recipients;

    private String subject;

    private String body;

    private Map<String, Object> metadata;
}
