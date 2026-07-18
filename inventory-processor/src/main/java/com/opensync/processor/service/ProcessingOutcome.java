package com.opensync.processor.service;
import com.opensync.contracts.*;
public record ProcessingOutcome(SyncStatus status,CanonicalInventory inventory){ }

