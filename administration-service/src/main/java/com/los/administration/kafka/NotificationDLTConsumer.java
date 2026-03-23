package com.los.administration.kafka;


import com.los.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationDLTConsumer {

    @KafkaListener(
            topics = "los.notification.DLT",
            groupId = "notification-dlt-group"
    )
    public void consumeDLT(NotificationEvent event) {

        log.error("🚨 DLT EVENT RECEIVED {}", event);

        // optional: store / alert / retry manually
    }
}