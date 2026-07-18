package com.opensync.contracts;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

/** Stable model shared by every ingress adapter and downstream consumer. */
public record CanonicalInventory(
        @NotBlank String partnerId,
        @NotBlank String partnerListingId,
        @NotBlank String eventId,
        String venueId,
        @NotBlank String section,
        @NotBlank String row,
        @Min(1) Integer seatStart,
        @Min(1) Integer seatEnd,
        @Min(0) int quantity,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
        @NotBlank String currency,
        @NotNull InventoryStatus status,
        @Min(0) long version,
        @NotNull Instant partnerUpdatedAt) {
}

