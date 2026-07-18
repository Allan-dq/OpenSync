package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="dead_letter_messages")
public class DeadLetterMessageEntity extends BaseEntity {
    @Id private UUID messageId;
    @Column(name="original_topic",nullable=false) private String originalTopic;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String payload;
    @Column(name="error_code",nullable=false) private String errorCode;
    @Column(name="error_message",nullable=false) private String errorMessage;
    @Column(name="stack_trace_summary") private String stackTraceSummary;
    @Column(name="retry_count",nullable=false) private int retryCount;
    @Column(name="first_failed_at",nullable=false) private Instant firstFailedAt;
    @Column(name="last_failed_at",nullable=false) private Instant lastFailedAt;
    @Column(nullable=false) private String status;
    protected DeadLetterMessageEntity() { }
    public DeadLetterMessageEntity(UUID id,String topic,String payload,String code,String message,String stack,int retries){messageId=id;originalTopic=topic;this.payload=payload;errorCode=code;errorMessage=message;stackTraceSummary=stack;retryCount=retries;firstFailedAt=lastFailedAt=Instant.now();status="PENDING";}
    public UUID getMessageId(){return messageId;} public String getPayload(){return payload;} public String getStatus(){return status;} public String getErrorCode(){return errorCode;} public String getErrorMessage(){return errorMessage;} public int getRetryCount(){return retryCount;}
    public void markReplaying(){status="REPLAYING";} public void resolve(){status="RESOLVED";}
}
