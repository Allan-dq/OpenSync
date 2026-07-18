package com.opensync.contracts;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        String eventVersion,
        String correlationId,
        String requestId,
        String partnerId,
        Instant occurredAt,
        T payload) {

    public static <T> EventEnvelope<T> create(String eventType, String correlationId, String requestId,
                                               String partnerId, T payload) {
        return new EventEnvelope<>(UUID.randomUUID(), eventType, "1.0", correlationId, requestId,
                partnerId, Instant.now(), payload);
    }
}

