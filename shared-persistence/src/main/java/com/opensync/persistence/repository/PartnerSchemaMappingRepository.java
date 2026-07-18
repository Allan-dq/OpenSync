package com.opensync.persistence.repository;
import com.opensync.persistence.entity.PartnerSchemaMappingEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PartnerSchemaMappingRepository extends JpaRepository<PartnerSchemaMappingEntity,UUID>{Optional<PartnerSchemaMappingEntity> findFirstByPartnerIdAndStatusOrderByMappingVersionDesc(String partnerId,String status);Optional<PartnerSchemaMappingEntity> findFirstByPartnerIdOrderByMappingVersionDesc(String partnerId);}
