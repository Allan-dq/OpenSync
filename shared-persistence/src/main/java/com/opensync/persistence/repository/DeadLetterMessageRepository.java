package com.opensync.persistence.repository;
import com.opensync.persistence.entity.DeadLetterMessageEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface DeadLetterMessageRepository extends JpaRepository<DeadLetterMessageEntity,UUID>{long countByStatus(String status);}
