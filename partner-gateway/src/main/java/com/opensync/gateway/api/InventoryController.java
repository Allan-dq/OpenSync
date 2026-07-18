package com.opensync.gateway.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.opensync.contracts.ErrorCode;
import com.opensync.gateway.service.InventoryIngressService;
import com.opensync.persistence.repository.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1")
public class InventoryController {
 private final InventoryIngressService ingress;private final InventoryRepository inventory;private final InventorySyncJobRepository jobs;
 public InventoryController(InventoryIngressService ingress,InventoryRepository inventory,InventorySyncJobRepository jobs){this.ingress=ingress;this.inventory=inventory;this.jobs=jobs;}
 @PostMapping("/partners/{partnerId}/inventory")
 ResponseEntity<IngressResponse> accept(@PathVariable String partnerId,@RequestHeader("Idempotency-Key") String key,@RequestHeader(value="X-Correlation-Id",required=false) String correlation,@RequestBody JsonNode payload){return ResponseEntity.accepted().body(ingress.accept(partnerId,key,correlation,payload));}
 @PostMapping("/partners/{partnerId}/inventory/bulk")
 ResponseEntity<BulkIngressResponse> bulk(@PathVariable String partnerId,@RequestHeader("Idempotency-Key") String key,@RequestHeader(value="X-Correlation-Id",required=false) String correlation,@RequestBody List<JsonNode> payloads){
  if(payloads.size()>1000)throw new GatewayException(ErrorCode.INVALID_PAYLOAD,"Bulk request exceeds 1000 items");int accepted=0;var errors=new ArrayList<String>();var batch="batch-"+UUID.randomUUID();
  for(int x=0;x<payloads.size();x++){try{ingress.accept(partnerId,key+":"+x,correlation,payloads.get(x));accepted++;}catch(RuntimeException e){errors.add("item "+x+": "+e.getMessage());}}
  return ResponseEntity.accepted().body(new BulkIngressResponse(batch,accepted,payloads.size()-accepted,errors));
 }
 @GetMapping("/inventory/{partnerId}/{listingId}") Object inventory(@PathVariable String partnerId,@PathVariable String listingId){return inventory.findByPartnerIdAndPartnerListingId(partnerId,listingId).map(i->i.toCanonical()).orElseThrow(()->new GatewayException(ErrorCode.INVALID_PAYLOAD,"Inventory not found"));}
 @GetMapping("/sync-jobs/{requestId}") Object job(@PathVariable String requestId){return jobs.findByRequestId(requestId).orElseThrow(()->new GatewayException(ErrorCode.INVALID_PAYLOAD,"Sync job not found"));}
 @GetMapping("/partners/{partnerId}/errors") Object errors(@PathVariable String partnerId){return jobs.findTop100ByPartnerIdAndErrorCodeIsNotNullOrderByCreatedAtDesc(partnerId);}
}

