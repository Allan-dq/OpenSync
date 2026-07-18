package com.opensync.gateway.ai;
import com.fasterxml.jackson.databind.JsonNode;import com.opensync.contracts.*;
public interface LlmClient{SchemaMappingSuggestion suggestMapping(JsonNode partnerPayload,CanonicalSchema canonicalSchema);FailureExplanation explainFailure(FailureContext context);}

