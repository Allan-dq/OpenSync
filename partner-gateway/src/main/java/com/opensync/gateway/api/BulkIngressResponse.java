package com.opensync.gateway.api;
import java.util.List;
public record BulkIngressResponse(String batchId,int accepted,int rejected,List<String> errors) { }

