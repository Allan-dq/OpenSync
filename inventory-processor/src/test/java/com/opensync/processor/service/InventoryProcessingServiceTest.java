package com.opensync.processor.service;

import static org.assertj.core.api.Assertions.assertThat;import static org.mockito.Mockito.*;
import com.fasterxml.jackson.databind.ObjectMapper;import com.opensync.contracts.*;import com.opensync.persistence.entity.*;import com.opensync.persistence.repository.*;
import java.math.BigDecimal;import java.time.Instant;import java.util.Optional;import org.junit.jupiter.api.*;

class InventoryProcessingServiceTest {
 private InventoryRepository inventories=mock(InventoryRepository.class);private InventorySyncJobRepository jobs=mock(InventorySyncJobRepository.class);private OutboxEventRepository outbox=mock(OutboxEventRepository.class);
 private CanonicalInventory item(long version){return new CanonicalInventory("partner-a","L1","E1",null,"101","A",1,2,2,new BigDecimal("50.00"),"USD",InventoryStatus.AVAILABLE,version,Instant.parse("2026-07-16T12:00:00Z"));}
 private EventEnvelope<CanonicalInventory> event(long v){return EventEnvelope.create("InventoryReceived","c","r","partner-a",item(v));}
 @BeforeEach void job(){when(jobs.findByRequestId("r")).thenReturn(Optional.of(new InventorySyncJobEntity("r","c","partner-a","L1",SyncStatus.RECEIVED)));}
 @Test void rejectsOlderVersion(){var stored=new InventoryEntity(item(12));when(inventories.findByPartnerIdAndPartnerListingId("partner-a","L1")).thenReturn(Optional.of(stored));var result=new InventoryProcessingService(inventories,jobs,outbox,new ObjectMapper()).process(event(11));assertThat(result.status()).isEqualTo(SyncStatus.STALE_REJECTED);verify(inventories,never()).save(any());}
 @Test void ignoresSameVersion(){var stored=new InventoryEntity(item(12));when(inventories.findByPartnerIdAndPartnerListingId("partner-a","L1")).thenReturn(Optional.of(stored));var result=new InventoryProcessingService(inventories,jobs,outbox,new ObjectMapper()).process(event(12));assertThat(result.status()).isEqualTo(SyncStatus.DUPLICATE_IGNORED);verify(outbox,never()).save(any());}
}
