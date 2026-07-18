package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="publication_attempts")
public class PublicationAttemptEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="message_id",nullable=false) private UUID messageId;
    @Column(name="attempt_number",nullable=false) private int attemptNumber;
    @Column(nullable=false) private String status;
    @Column(name="http_status") private Integer httpStatus;
    @Column(name="error_message") private String errorMessage;
    @Column(name="duration_ms",nullable=false) private long durationMs;
    @Column(name="attempted_at",nullable=false) private Instant attemptedAt=Instant.now();
    protected PublicationAttemptEntity() { }
    public PublicationAttemptEntity(UUID messageId,int attempt,String status,Integer httpStatus,String error,long duration){this.messageId=messageId;this.attemptNumber=attempt;this.status=status;this.httpStatus=httpStatus;this.errorMessage=error;this.durationMs=duration;}
}
