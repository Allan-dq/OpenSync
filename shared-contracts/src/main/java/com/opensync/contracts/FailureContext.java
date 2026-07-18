package com.opensync.contracts;
import java.util.List;
public record FailureContext(String errorCode,List<String> validationErrors,String sanitizedPayload,String processingStage,List<String> retryHistory){ }

