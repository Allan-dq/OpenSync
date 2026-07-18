package com.opensync.gateway.service;
import com.fasterxml.jackson.databind.*;import com.opensync.contracts.*;import com.opensync.gateway.api.GatewayException;import com.opensync.persistence.entity.OutboxEventEntity;import com.opensync.persistence.repository.*;import java.util.UUID;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
@Service
public class DeadLetterReplayService{
 private final DeadLetterMessageRepository dlq;private final OutboxEventRepository outbox;private final ObjectMapper json;
 public DeadLetterReplayService(DeadLetterMessageRepository d,OutboxEventRepository o,ObjectMapper j){dlq=d;outbox=o;json=j;}
 @Transactional public void replay(UUID id){var message=dlq.findById(id).orElseThrow(()->new GatewayException(ErrorCode.INVALID_PAYLOAD,"Dead-letter message not found"));try{var type=json.getTypeFactory().constructParametricType(EventEnvelope.class,CanonicalInventory.class);EventEnvelope<CanonicalInventory> event=json.readValue(message.getPayload(),type);message.markReplaying();outbox.save(new OutboxEventEntity(UUID.randomUUID(),TopicNames.INVENTORY_UPDATED,"replay:"+id,message.getPayload()));}catch(Exception e){throw new GatewayException(ErrorCode.INTERNAL_PROCESSING_ERROR,"Dead-letter payload cannot be replayed");}}
}

