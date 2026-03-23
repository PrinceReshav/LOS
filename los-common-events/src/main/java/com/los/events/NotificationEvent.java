package com.los.events;

import java.util.List;
import java.util.Map;

public record NotificationEvent(
        String eventId,
        String type,              // EMAIL / SMS
        String templateCode,
        List<String> recipients,
        Map<String, Object> metadata
) {}