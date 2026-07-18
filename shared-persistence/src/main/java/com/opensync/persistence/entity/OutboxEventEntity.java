package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="outbox_events")
public class OutboxEventEntity extends BaseEntity {
    @Id private UUID id;
    @Column(nullable=false) private String topic;
    @Column(name="event_key",nullable=false) private String eventKey;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String payload;
    @Column(nullable=false) private String status="PENDING";
    @Column(name="attempt_count",nullable=false) private int attemptCount;
    @Column(name="published_at") private Instant publishedAt;
    protected OutboxEventEntity() { }
    public OutboxEventEntity(UUID id,String topic,String eventKey,String payload){this.id=id;this.topic=topic;this.eventKey=eventKey;this.payload=payload;}
    public UUID getId(){return id;} public String getTopic(){return topic;} public String getEventKey(){return eventKey;} public String getPayload(){return payload;} public String getStatus(){return status;}
    public void published(){status="PUBLISHED";publishedAt=Instant.now();} public void failedAttempt(){attemptCount++;}
}
