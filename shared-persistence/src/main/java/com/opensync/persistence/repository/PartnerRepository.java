package com.opensync.persistence.repository;
import com.opensync.persistence.entity.PartnerEntity; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PartnerRepository extends JpaRepository<PartnerEntity,UUID>{Optional<PartnerEntity> findByPartnerIdAndActiveTrue(String partnerId);}
