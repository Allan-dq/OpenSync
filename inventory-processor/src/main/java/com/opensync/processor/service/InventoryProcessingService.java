package com.opensync.processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensync.contracts.*;
import com.opensync.persistence.entity.*;
import com.opensync.persistence.repository.*;
import java.util.UUID;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryProcessingService {
 private final InventoryRepository inventory;private final InventorySyncJobRepository jobs;private final OutboxEventRepository outbox;private final ObjectMapper json;
 public InventoryProcessingService(InventoryRepository i,InventorySyncJobRepository j,OutboxEventRepository o,ObjectMapper json){inventory=i;jobs=j;outbox=o;this.json=json;}
 @Transactional
 public ProcessingOutcome process(EventEnvelope<CanonicalInventory> received){
  var incoming=received.payload();var job=jobs.findByRequestId(received.requestId()).orElseThrow(()->new IllegalStateException("Missing sync job "+received.requestId()));
  job.transitionTo(SyncStatus.VALIDATED);
  var current=inventory.findByPartnerIdAndPartnerListingId(incoming.partnerId(),incoming.partnerListingId());
  if(current.isPresent()&&current.get().getSourceVersion()>incoming.version()){job.fail(SyncStatus.STALE_REJECTED,ErrorCode.STALE_INVENTORY_VERSION.name(),"A newer source version is already stored");return new ProcessingOutcome(SyncStatus.STALE_REJECTED,current.get().toCanonical());}
  if(current.isPresent()&&current.get().getSourceVersion()==incoming.version()){job.transitionTo(SyncStatus.DUPLICATE_IGNORED);return new ProcessingOutcome(SyncStatus.DUPLICATE_IGNORED,current.get().toCanonical());}
  var entity=current.orElseGet(()->new InventoryEntity(incoming));if(current.isPresent())entity.apply(incoming);entity.setSyncStatus(SyncStatus.PERSISTED);inventory.save(entity);job.transitionTo(SyncStatus.PERSISTED);
  try{var updated=EventEnvelope.create("InventoryUpdated",received.correlationId(),received.requestId(),received.partnerId(),incoming);outbox.save(new OutboxEventEntity(updated.eventId(),TopicNames.INVENTORY_UPDATED,incoming.partnerId()+":"+incoming.partnerListingId(),json.writeValueAsString(updated)));}
  catch(Exception e){throw new IllegalStateException("Unable to create inventory.updated outbox event",e);}
  return new ProcessingOutcome(SyncStatus.PERSISTED,incoming);
 }
}

