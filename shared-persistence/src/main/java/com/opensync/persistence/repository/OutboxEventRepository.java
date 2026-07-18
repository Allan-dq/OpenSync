package com.opensync.persistence.repository;
import com.opensync.persistence.entity.OutboxEventEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity,UUID>{List<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(String status);}
