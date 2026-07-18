package com.opensync.contracts;

import java.time.Instant;
import java.util.List;

public record ApiError(Instant timestamp, String correlationId, String errorCode, String message,
                       List<FieldError> fieldErrors) {
    public record FieldError(String field, String reason) { }
}

