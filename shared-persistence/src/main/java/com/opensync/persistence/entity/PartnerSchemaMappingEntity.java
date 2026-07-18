package com.opensync.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="partner_schema_mappings", uniqueConstraints=@UniqueConstraint(columnNames={"partner_id","mapping_version"}))
public class PartnerSchemaMappingEntity extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name="partner_id", nullable=false) private String partnerId;
    @Column(name="mapping_version", nullable=false) private int mappingVersion;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="mapping_json", nullable=false, columnDefinition="jsonb") private String mappingJson;
    @Column(nullable=false) private String status;
    @Column(name="approved_by") private String approvedBy;
    protected PartnerSchemaMappingEntity() { }
    public PartnerSchemaMappingEntity(String partnerId,int mappingVersion,String mappingJson,String status){this.partnerId=partnerId;this.mappingVersion=mappingVersion;this.mappingJson=mappingJson;this.status=status;}
    public UUID getId(){return id;} public String getPartnerId(){return partnerId;} public int getMappingVersion(){return mappingVersion;}
    public String getMappingJson(){return mappingJson;} public String getStatus(){return status;}
    public void approve(String approvedBy){this.status="APPROVED";this.approvedBy=approvedBy;}
}
