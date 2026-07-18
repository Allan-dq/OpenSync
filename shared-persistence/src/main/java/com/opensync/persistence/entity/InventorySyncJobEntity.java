package com.opensync.persistence.entity;

import com.opensync.contracts.SyncStatus;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="inventory_sync_jobs")
public class InventorySyncJobEntity extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="request_id",nullable=false,unique=true) private String requestId;
    @Column(name="correlation_id",nullable=false) private String correlationId;
    @Column(name="partner_id",nullable=false) private String partnerId;
    @Column(name="partner_listing_id") private String partnerListingId;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private SyncStatus status;
    @Column(name="error_code") private String errorCode;
    @Column(name="error_message") private String errorMessage;
    protected InventorySyncJobEntity() { }
    public InventorySyncJobEntity(String requestId,String correlationId,String partnerId,String listingId,SyncStatus status){this.requestId=requestId;this.correlationId=correlationId;this.partnerId=partnerId;this.partnerListingId=listingId;this.status=status;}
    public String getRequestId(){return requestId;} public String getCorrelationId(){return correlationId;} public String getPartnerId(){return partnerId;} public SyncStatus getStatus(){return status;}
    public void transitionTo(SyncStatus status){this.status=status;} public void fail(SyncStatus status,String code,String message){this.status=status;this.errorCode=code;this.errorMessage=message;}
}

