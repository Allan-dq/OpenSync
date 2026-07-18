package com.opensync.gateway.api;
import com.fasterxml.jackson.databind.JsonNode;import com.opensync.gateway.ai.SchemaMappingService;import com.opensync.gateway.ai.SchemaMappingService.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/partners/{partnerId}/schema-mappings")
public class SchemaMappingController{
 private final SchemaMappingService service;public SchemaMappingController(SchemaMappingService s){service=s;}
 @PostMapping("/preview") Preview preview(@PathVariable String partnerId,@RequestBody JsonNode sample){return service.preview(sample);}
 @PutMapping Object save(@PathVariable String partnerId,@RequestHeader("X-Approved-By") String approvedBy,@RequestBody SaveRequest request){return service.save(partnerId,request.sample(),request.suggestion(),approvedBy);}
}

