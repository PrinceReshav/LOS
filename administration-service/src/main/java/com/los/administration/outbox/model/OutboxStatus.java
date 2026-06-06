package com.los.administration.outbox.model;

public enum OutboxStatus {

    PENDING,
    GRPC_SUCCESS,
    KAFKA_PUBLISHED,
    FAILED
}