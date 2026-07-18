package com.opensync.publisher.service;

import com.opensync.contracts.*;import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.retry.annotation.*;import org.springframework.retry.support.RetrySynchronizationManager;import org.springframework.stereotype.Service;

@Service
public class MarketplacePublicationService{
 private final MarketplaceClient client;private final PublicationAttemptRecorder recorder;private final PublicationStateService state;private final MeterRegistry metrics;
 public MarketplacePublicationService(MarketplaceClient c,PublicationAttemptRecorder r,PublicationStateService state,MeterRegistry m){client=c;recorder=r;this.state=state;metrics=m;}
 @Retryable(retryFor=MarketplaceUnavailableException.class,maxAttempts=4,backoff=@Backoff(delay=1000,multiplier=2))
 public void publish(EventEnvelope<CanonicalInventory> event,String originalPayload){
  int attempt=RetrySynchronizationManager.getContext()==null?1:RetrySynchronizationManager.getContext().getRetryCount()+1;long start=System.nanoTime();
  try{client.publish(event.payload());recorder.record(event.eventId(),attempt,"SUCCESS",200,null,elapsed(start),event.requestId());state.complete(event);metrics.counter("inventory_published_total").increment();}
  catch(MarketplaceUnavailableException e){recorder.record(event.eventId(),attempt,"FAILED",null,e.getMessage(),elapsed(start),event.requestId());metrics.counter("inventory_failed_total","stage","publishing").increment();throw e;}
 }
 @Recover public void recover(MarketplaceUnavailableException failure,EventEnvelope<CanonicalInventory> event,String originalPayload){
  state.deadLetter(event,originalPayload,failure);metrics.counter("inventory_dead_lettered_total").increment();
 }
 private long elapsed(long start){return java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start);}
}
