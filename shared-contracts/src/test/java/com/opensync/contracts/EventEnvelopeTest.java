package com.opensync.contracts;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class EventEnvelopeTest {
    @Test void createsTraceableVersionedEnvelope() {
        var envelope = EventEnvelope.create("InventoryReceived", "corr-1", "req-1", "partner-a", "payload");
        assertThat(envelope.eventId()).isNotNull();
        assertThat(envelope.eventVersion()).isEqualTo("1.0");
        assertThat(envelope.correlationId()).isEqualTo("corr-1");
    }
}
