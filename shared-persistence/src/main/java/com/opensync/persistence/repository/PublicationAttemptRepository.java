package com.opensync.persistence.repository;
import com.opensync.persistence.entity.PublicationAttemptEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PublicationAttemptRepository extends JpaRepository<PublicationAttemptEntity,UUID>{}

