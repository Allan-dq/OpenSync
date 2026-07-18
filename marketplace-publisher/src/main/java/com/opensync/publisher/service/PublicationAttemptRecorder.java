package com.opensync.publisher.service;
import com.opensync.contracts.SyncStatus;import com.opensync.persistence.entity.PublicationAttemptEntity;import com.opensync.persistence.repository.*;import java.util.UUID;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.*;
@Service
public class PublicationAttemptRecorder{
 private final PublicationAttemptRepository attempts;private final InventorySyncJobRepository jobs;
 public PublicationAttemptRecorder(PublicationAttemptRepository a,InventorySyncJobRepository j){attempts=a;jobs=j;}
 @Transactional(propagation=Propagation.REQUIRES_NEW) public void record(UUID id,int attempt,String status,Integer http,String error,long duration,String requestId){attempts.save(new PublicationAttemptEntity(id,attempt,status,http,error,duration));jobs.findByRequestId(requestId).ifPresent(j->j.transitionTo("SUCCESS".equals(status)?SyncStatus.PUBLISHED:SyncStatus.RETRYING));}
}

