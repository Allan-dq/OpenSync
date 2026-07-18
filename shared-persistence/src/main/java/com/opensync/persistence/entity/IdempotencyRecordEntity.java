package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="idempotency_records",uniqueConstraints=@UniqueConstraint(columnNames={"partner_id","idempotency_key"}))
public class IdempotencyRecordEntity extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="partner_id",nullable=false) private String partnerId;
    @Column(name="idempotency_key",nullable=false) private String idempotencyKey;
    @Column(name="request_id",nullable=false) private String requestId;
    @Column(name="correlation_id",nullable=false) private String correlationId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="response_json",nullable=false,columnDefinition="jsonb") private String responseJson;
    @Column(name="expires_at",nullable=false) private Instant expiresAt;
    protected IdempotencyRecordEntity() { }
    public IdempotencyRecordEntity(String partnerId,String key,String requestId,String correlationId,String responseJson,Instant expiresAt){this.partnerId=partnerId;this.idempotencyKey=key;this.requestId=requestId;this.correlationId=correlationId;this.responseJson=responseJson;this.expiresAt=expiresAt;}
    public String getRequestId(){return requestId;} public String getCorrelationId(){return correlationId;} public String getResponseJson(){return responseJson;}
}
