package com.opensync.persistence.repository;
import com.opensync.persistence.entity.InventoryEntity; import java.util.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface InventoryRepository extends JpaRepository<InventoryEntity,UUID>{
 Optional<InventoryEntity> findByPartnerIdAndPartnerListingId(String partnerId,String listingId);
 @Query("select i from InventoryEntity i where i.partnerId=:partnerId and i.partnerListingId=:listingId") Optional<InventoryEntity> findForUpdate(@Param("partnerId") String partnerId,@Param("listingId") String listingId);
}
