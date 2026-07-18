package com.opensync.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.opensync.contracts.*;
import com.opensync.gateway.api.IngressResponse;
import com.opensync.persistence.entity.*;
import com.opensync.persistence.repository.*;
import java.time.*;
import java.util.UUID;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryIngressService {
 private final PartnerPayloadNormalizer normalizer; private final IdempotencyRecordRepository idempotency;
 private final InventorySyncJobRepository jobs; private final OutboxEventRepository outbox; private final ObjectMapper json;private final MeterRegistry metrics;
 public InventoryIngressService(PartnerPayloadNormalizer n,IdempotencyRecordRepository i,InventorySyncJobRepository j,OutboxEventRepository o,ObjectMapper json,MeterRegistry metrics){normalizer=n;idempotency=i;jobs=j;outbox=o;this.json=json;this.metrics=metrics;}
 @Transactional
 public IngressResponse accept(String partnerId,String key,String requestedCorrelation,JsonNode payload){
  if(key==null||key.isBlank())throw new com.opensync.gateway.api.GatewayException(ErrorCode.INVALID_PAYLOAD,"Idempotency-Key header is required");
  var old=idempotency.findByPartnerIdAndIdempotencyKey(partnerId,key); if(old.isPresent())return read(old.get().getResponseJson());
  var inventory=normalizer.normalize(partnerId,payload);var requestId="req-"+UUID.randomUUID();
  var correlationId=requestedCorrelation==null||requestedCorrelation.isBlank()?"corr-"+UUID.randomUUID():requestedCorrelation;
  var response=new IngressResponse(requestId,correlationId,"ACCEPTED","Inventory update accepted for asynchronous processing");
  var event=EventEnvelope.create("InventoryReceived",correlationId,requestId,partnerId,inventory);
  try {
   var responseJson=json.writeValueAsString(response);var eventJson=json.writeValueAsString(event);
   jobs.save(new InventorySyncJobEntity(requestId,correlationId,partnerId,inventory.partnerListingId(),SyncStatus.RECEIVED));
   idempotency.save(new IdempotencyRecordEntity(partnerId,key,requestId,correlationId,responseJson,Instant.now().plus(Duration.ofHours(24))));
   outbox.save(new OutboxEventEntity(event.eventId(),TopicNames.INVENTORY_RECEIVED,partnerId+":"+inventory.partnerListingId(),eventJson));
   metrics.counter("inventory_ingested_total","partner",partnerId).increment();metrics.counter("partner_request_rate","partner",partnerId).increment();return response;
  }catch(JsonProcessingException e){throw new com.opensync.gateway.api.GatewayException(ErrorCode.INTERNAL_PROCESSING_ERROR,"Could not serialize inventory event");}
 }
 private IngressResponse read(String value){try{return json.readValue(value,IngressResponse.class);}catch(JsonProcessingException e){throw new IllegalStateException("Invalid stored idempotency response",e);}}
}
