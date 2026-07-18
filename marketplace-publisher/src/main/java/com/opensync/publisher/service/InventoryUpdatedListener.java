package com.opensync.publisher.service;
import com.fasterxml.jackson.databind.ObjectMapper;import com.opensync.contracts.*;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
@Component
public class InventoryUpdatedListener{
 private final ObjectMapper json;private final MarketplacePublicationService service;public InventoryUpdatedListener(ObjectMapper j,MarketplacePublicationService s){json=j;service=s;}
 @KafkaListener(topics=TopicNames.INVENTORY_UPDATED,groupId="marketplace-publisher") public void receive(String value)throws Exception{var type=json.getTypeFactory().constructParametricType(EventEnvelope.class,CanonicalInventory.class);EventEnvelope<CanonicalInventory> event=json.readValue(value,type);service.publish(event,value);}
}

