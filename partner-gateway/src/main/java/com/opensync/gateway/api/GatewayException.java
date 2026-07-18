package com.opensync.gateway.api;
import com.opensync.contracts.ErrorCode;
public class GatewayException extends RuntimeException {
 private final ErrorCode code;
 public GatewayException(ErrorCode code,String message){super(message);this.code=code;}
 public ErrorCode getCode(){return code;}
}

