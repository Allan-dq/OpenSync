package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "partners")
public class PartnerEntity extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="partner_id", nullable=false, unique=true) private String partnerId;
    @Column(nullable=false) private String name;
    @Column(name="api_key_hash", nullable=false) private String apiKeyHash;
    @Column(name="hmac_secret_hash") private String hmacSecretHash;
    @Column(nullable=false) private boolean active = true;
    protected PartnerEntity() { }
    public PartnerEntity(String partnerId, String name, String apiKeyHash) { this.partnerId=partnerId; this.name=name; this.apiKeyHash=apiKeyHash; }
    public UUID getId(){return id;} public String getPartnerId(){return partnerId;} public String getName(){return name;}
    public String getApiKeyHash(){return apiKeyHash;} public boolean isActive(){return active;}
}

