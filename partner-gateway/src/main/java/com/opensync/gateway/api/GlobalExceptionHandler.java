package com.opensync.gateway.api;

import com.opensync.contracts.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
 private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
 @ExceptionHandler(GatewayException.class)
 ResponseEntity<ApiError> gateway(GatewayException ex,HttpServletRequest request){
  var status=ex.getCode().name().contains("API_KEY")?HttpStatus.UNAUTHORIZED:HttpStatus.BAD_REQUEST;
  return ResponseEntity.status(status).body(new ApiError(Instant.now(),correlation(request),ex.getCode().name(),ex.getMessage(),List.of()));
 }
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex,HttpServletRequest request){
  var fields=ex.getBindingResult().getFieldErrors().stream().map(e->new ApiError.FieldError(e.getField(),e.getDefaultMessage())).toList();
  return ResponseEntity.badRequest().body(new ApiError(Instant.now(),correlation(request),"INVALID_PAYLOAD","Request validation failed",fields));
 }
 @ExceptionHandler(Exception.class)
 ResponseEntity<ApiError> unexpected(Exception ex,HttpServletRequest request){
  log.error("Unhandled request failure correlationId={} method={} path={}",correlation(request),request.getMethod(),request.getRequestURI(),ex);
  return ResponseEntity.internalServerError().body(new ApiError(Instant.now(),correlation(request),"INTERNAL_PROCESSING_ERROR","The request could not be processed",List.of()));
 }
 private String correlation(HttpServletRequest r){var v=r.getHeader("X-Correlation-Id");return v==null||v.isBlank()?"unknown":v;}
}
