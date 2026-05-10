package com.los.administration.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationRequest {

    private String templateCode;

    private List<String> recipients;

    private Map<String, Object> metadata;

}