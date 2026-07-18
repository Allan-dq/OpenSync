package com.opensync.persistence.entity;

import com.opensync.contracts.CanonicalInventory;
import com.opensync.contracts.InventoryStatus;
import com.opensync.contracts.SyncStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="inventory", uniqueConstraints=@UniqueConstraint(columnNames={"partner_id","partner_listing_id"}))
public class InventoryEntity extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="partner_id",nullable=false) private String partnerId;
    @Column(name="partner_listing_id",nullable=false) private String partnerListingId;
    @Column(name="event_id",nullable=false) private String eventId;
    @Column(name="venue_id") private String venueId;
    @Column(nullable=false) private String section;
    @Column(name="row_number",nullable=false) private String rowNumber;
    @Column(name="seat_start") private Integer seatStart;
    @Column(name="seat_end") private Integer seatEnd;
    @Column(nullable=false) private int quantity;
    @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
    @Column(nullable=false,length=3) private String currency;
    @Enumerated(EnumType.STRING) @Column(name="inventory_status",nullable=false) private InventoryStatus inventoryStatus;
    @Column(name="source_version",nullable=false) private long sourceVersion;
    @Column(name="partner_updated_at",nullable=false) private Instant partnerUpdatedAt;
    @Enumerated(EnumType.STRING) @Column(name="sync_status",nullable=false) private SyncStatus syncStatus;
    @Version @Column(name="entity_version",nullable=false) private long entityVersion;
    protected InventoryEntity() { }
    public InventoryEntity(CanonicalInventory i){apply(i);this.syncStatus=SyncStatus.PERSISTED;}
    public void apply(CanonicalInventory i){partnerId=i.partnerId();partnerListingId=i.partnerListingId();eventId=i.eventId();venueId=i.venueId();section=i.section();rowNumber=i.row();seatStart=i.seatStart();seatEnd=i.seatEnd();quantity=i.quantity();unitPrice=i.unitPrice();currency=i.currency();inventoryStatus=i.status();sourceVersion=i.version();partnerUpdatedAt=i.partnerUpdatedAt();}
    public UUID getId(){return id;} public String getPartnerId(){return partnerId;} public String getPartnerListingId(){return partnerListingId;}
    public long getSourceVersion(){return sourceVersion;} public long getEntityVersion(){return entityVersion;} public SyncStatus getSyncStatus(){return syncStatus;}
    public void setSyncStatus(SyncStatus status){this.syncStatus=status;}
    public CanonicalInventory toCanonical(){return new CanonicalInventory(partnerId,partnerListingId,eventId,venueId,section,rowNumber,seatStart,seatEnd,quantity,unitPrice,currency,inventoryStatus,sourceVersion,partnerUpdatedAt);}
}

