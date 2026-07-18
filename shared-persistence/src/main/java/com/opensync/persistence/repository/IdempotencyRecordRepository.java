package com.opensync.persistence.repository;
import com.opensync.persistence.entity.IdempotencyRecordEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecordEntity,UUID>{Optional<IdempotencyRecordEntity> findByPartnerIdAndIdempotencyKey(String partnerId,String key);}
