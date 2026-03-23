package com.los.administration.notification.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotificationRequest {

    private String templateCode;

    private List<String> recipients;

    private Map<String, Object> metadata;
}