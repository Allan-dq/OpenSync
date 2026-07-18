package com.opensync.marketplace.api;
import com.opensync.contracts.CanonicalInventory;import java.util.*;import java.util.concurrent.ConcurrentHashMap;import java.util.concurrent.atomic.AtomicReference;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/marketplace/v1")
public class MarketplaceController{
 public record FailureMode(boolean enabled,double failureRate,long latencyMs){public FailureMode{if(failureRate<0||failureRate>1)throw new IllegalArgumentException("failureRate must be between 0 and 1");if(latencyMs<0||latencyMs>30000)throw new IllegalArgumentException("latencyMs must be between 0 and 30000");}}
 private final AtomicReference<FailureMode> mode=new AtomicReference<>(new FailureMode(false,0,0));private final Map<String,CanonicalInventory> listings=new ConcurrentHashMap<>();
 @PostMapping("/listings") ResponseEntity<Void> publish(@RequestBody CanonicalInventory item)throws InterruptedException{var f=mode.get();if(f.latencyMs()>0)Thread.sleep(f.latencyMs());if(f.enabled()&&Math.random()<f.failureRate())return ResponseEntity.status(503).build();listings.put(item.partnerId()+":"+item.partnerListingId(),item);return ResponseEntity.accepted().build();}
 @PostMapping("/admin/failure-mode") FailureMode configure(@RequestBody FailureMode requested){mode.set(requested);return requested;}
 @GetMapping("/listings/{partnerId}/{listingId}") CanonicalInventory get(@PathVariable String partnerId,@PathVariable String listingId){return listings.get(partnerId+":"+listingId);}
}

