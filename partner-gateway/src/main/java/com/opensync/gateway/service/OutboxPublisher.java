package com.opensync.gateway.service;

import com.opensync.persistence.repository.OutboxEventRepository;
import org.slf4j.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
 private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class);
 private final OutboxEventRepository events; private final KafkaTemplate<String,String> kafka;
 public OutboxPublisher(OutboxEventRepository events,KafkaTemplate<String,String> kafka){this.events=events;this.kafka=kafka;}
 @Scheduled(fixedDelayString="${opensync.outbox.interval-ms:250}") @Transactional
 public void publish(){for(var event:events.findTop100ByStatusOrderByCreatedAtAsc("PENDING")){try{kafka.send(event.getTopic(),event.getEventKey(),event.getPayload()).get();event.published();}catch(Exception e){event.failedAttempt();log.warn("outbox publish failed eventId={} topic={}",event.getId(),event.getTopic());}}}
}

