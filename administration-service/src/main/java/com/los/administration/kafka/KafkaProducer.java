package com.los.administration.kafka;

import com.los.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaProducer.class);

    private static final String USER_CREATED_TOPIC =
            "los.user.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String,Object>>
    publishUserCreated(
            UserCreatedEvent event
    ){

        return kafkaTemplate.send(
                USER_CREATED_TOPIC,
                event.userId(),
                event
        ).whenComplete((result, ex) -> {

            if(ex != null){

                log.error(
                        "Failed to publish UserCreatedEvent userId={}",
                        event.userId(),
                        ex
                );

            } else {

                log.info(
                        "UserCreatedEvent published userId={} partition={}",
                        event.userId(),
                        result.getRecordMetadata()
                                .partition()
                );
            }
        });
    }
}