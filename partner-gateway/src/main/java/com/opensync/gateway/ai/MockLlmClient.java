package com.opensync.gateway.ai;
import com.fasterxml.jackson.databind.JsonNode;import com.opensync.contracts.*;import com.opensync.contracts.SchemaMappingSuggestion.FieldMapping;import java.util.*;import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;import org.springframework.stereotype.Component;
@Component @ConditionalOnProperty(name="opensync.llm.provider",havingValue="mock",matchIfMissing=true)
public class MockLlmClient implements LlmClient{
 public SchemaMappingSuggestion suggestMapping(JsonNode p,CanonicalSchema schema){
  if(p.has("external_listing_code"))return new SchemaMappingSuggestion(List.of(m("$.external_listing_code","partnerListingId",TransformationType.DIRECT),m("$.event_code","eventId",TransformationType.DIRECT),m("$.seat_location.area","section",TransformationType.DIRECT),m("$.seat_location.line","row",TransformationType.DIRECT),m("$.seat_location.from","seatStart",TransformationType.DIRECT),m("$.seat_location.to","seatEnd",TransformationType.DIRECT),m("$.number_available","quantity",TransformationType.DIRECT),m("$.face_value.amount","unitPrice",TransformationType.DIVIDE_BY_100),m("$.face_value.currency_code","currency",TransformationType.DIRECT),m("$.inventory_state","status",TransformationType.ENUM_MAPPING),m("$.revision_number","version",TransformationType.DIRECT),m("$.last_modified","partnerUpdatedAt",TransformationType.DATE_FORMAT)),.96,List.of("Review inventory_state enum mapping before approval"));
  return new SchemaMappingSuggestion(schema.fields().stream().filter(JsonField->p.has(JsonField.name())).map(f->m("$."+f.name(),f.name(),TransformationType.DIRECT)).toList(),.75,List.of("Mock provider matched fields by name only"));
 }
 public FailureExplanation explainFailure(FailureContext c){var price=c.sanitizedPayload().contains("CENT");return new FailureExplanation(price?"The partner price appears to be expressed in cents.":"The message failed during "+c.processingStage(),price?"Incorrect price transformation":c.errorCode(),price?"Use DIVIDE_BY_100 for unitPrice.":"Review the mapping and retry history.",price?.91:.70);}
 private FieldMapping m(String s,String t,TransformationType x){return new FieldMapping(s,t,x);}
}

