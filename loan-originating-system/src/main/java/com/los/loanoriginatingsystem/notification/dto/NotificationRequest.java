package com.los.loanoriginatingsystem.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Shape MUST match administration-service's own
 * com.los.administration.notification.dto.NotificationRequest exactly -
 * that service resolves subject/body/type from a NotificationTemplate
 * looked up by templateCode and merges `metadata` into it via {{placeholder}}
 * substitution (see TemplateEngine). Free-text subject/body sent from here
 * would be silently ignored (Jackson drops unknown fields by default) and
 * templateCode would be missing, causing the send to fail server-side.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private String templateCode;

    private List<String> recipients;

    private Map<String, Object> metadata;
}
