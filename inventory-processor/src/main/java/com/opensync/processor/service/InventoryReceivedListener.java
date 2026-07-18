package com.opensync.processor.service;

import com.fasterxml.jackson.databind.*;import com.opensync.contracts.*;import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.*;import org.springframework.data.redis.core.StringRedisTemplate;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;

@Component
public class InventoryReceivedListener {
 private static final Logger log=LoggerFactory.getLogger(InventoryReceivedListener.class);
 private final ObjectMapper json;private final InventoryProcessingService service;private final StringRedisTemplate redis;private final MeterRegistry metrics;
 public InventoryReceivedListener(ObjectMapper j,InventoryProcessingService s,StringRedisTemplate r,MeterRegistry m){json=j;service=s;redis=r;metrics=m;}
 @KafkaListener(topics=TopicNames.INVENTORY_RECEIVED,groupId="inventory-processor")
 public void receive(String value)throws Exception{
  var type=json.getTypeFactory().constructParametricType(EventEnvelope.class,CanonicalInventory.class);EventEnvelope<CanonicalInventory> event=json.readValue(value,type);
  var started=System.nanoTime();var result=service.process(event);metrics.counter("inventory_processed_total","status",result.status().name()).increment();
  if(result.status()==SyncStatus.DUPLICATE_IGNORED)metrics.counter("inventory_duplicate_total").increment();
  if(result.status()==SyncStatus.STALE_REJECTED)metrics.counter("inventory_stale_update_total").increment();
  if(result.status()==SyncStatus.PERSISTED){try{redis.opsForValue().set("inventory:"+result.inventory().partnerId()+":"+result.inventory().partnerListingId(),json.writeValueAsString(result.inventory()));}catch(Exception e){log.warn("redis cache update failed partnerId={} listingId={}",result.inventory().partnerId(),result.inventory().partnerListingId());}}
  metrics.timer("inventory_processing_latency_ms").record(System.nanoTime()-started,java.util.concurrent.TimeUnit.NANOSECONDS);
 }
}

