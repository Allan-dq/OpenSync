package com.opensync.persistence.repository;
import com.opensync.persistence.entity.InventorySyncJobEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface InventorySyncJobRepository extends JpaRepository<InventorySyncJobEntity,UUID>{Optional<InventorySyncJobEntity> findByRequestId(String requestId);List<InventorySyncJobEntity> findTop100ByPartnerIdAndErrorCodeIsNotNullOrderByCreatedAtDesc(String partnerId);}
