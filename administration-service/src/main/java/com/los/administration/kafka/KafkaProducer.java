package com.los.administration.kafka;

import com.los.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    private static final String USER_CREATED_TOPIC = "los.user.created";

    public void publishUserCreated(UserCreatedEvent event) {

        kafkaTemplate.send(
                USER_CREATED_TOPIC,
                event.userId(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {

                log.error("Failed to publish UserCreatedEvent for userId={}",
                        event.userId(), ex);

            } else {

                log.info("UserCreatedEvent published successfully userId={} partition={}",
                        event.userId(),
                        result.getRecordMetadata().partition());
            }

        });
    }
}