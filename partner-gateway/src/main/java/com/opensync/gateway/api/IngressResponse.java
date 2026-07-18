package com.opensync.gateway.api;
public record IngressResponse(String requestId,String correlationId,String status,String message) { }

