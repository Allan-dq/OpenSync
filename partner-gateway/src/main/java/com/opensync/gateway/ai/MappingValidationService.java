package com.opensync.gateway.ai;
import com.fasterxml.jackson.databind.*;import com.fasterxml.jackson.databind.node.ObjectNode;import com.opensync.contracts.*;import java.math.BigDecimal;import java.util.*;import org.springframework.stereotype.Service;
@Service
public class MappingValidationService{
 private static final Set<String> REQUIRED=Set.of("partnerListingId","eventId","section","row","quantity","unitPrice","currency","status","version","partnerUpdatedAt");private final ObjectMapper json;
 public MappingValidationService(ObjectMapper json){this.json=json;}
 public ValidationResult validate(JsonNode sample,SchemaMappingSuggestion suggestion){var errors=new ArrayList<String>();var targets=new HashSet<String>();var transformed=json.createObjectNode();
  for(var m:suggestion.mappings()){if(!targets.add(m.targetField())){errors.add("Duplicate target field: "+m.targetField());continue;}try{var source=resolve(sample,m.sourcePath());if(source.isMissingNode()||source.isNull())throw new IllegalArgumentException("source path not found");transformed.set(m.targetField(),transform(source,m.transformation()));}catch(Exception e){errors.add(m.sourcePath()+" -> "+m.targetField()+": "+e.getMessage());}}
  REQUIRED.stream().filter(r->!targets.contains(r)).sorted().forEach(r->errors.add("Missing required target field: "+r));return new ValidationResult(errors.isEmpty(),errors,transformed);
 }
 private JsonNode resolve(JsonNode root,String path){if(!path.startsWith("$."))throw new IllegalArgumentException("Only JSON paths beginning with $. are allowed");var n=root;for(var part:path.substring(2).split("\\."))n=n.path(part);return n;}
 private JsonNode transform(JsonNode n,TransformationType t){return switch(t){case DIRECT,DATE_FORMAT->n.deepCopy();case STRING_TO_INTEGER->json.getNodeFactory().numberNode(Integer.parseInt(n.asText()));case STRING_TO_DECIMAL->json.getNodeFactory().numberNode(new BigDecimal(n.asText()));case DIVIDE_BY_100->json.getNodeFactory().numberNode(new BigDecimal(n.asText()).divide(BigDecimal.valueOf(100)));case ARRAY_LENGTH->json.getNodeFactory().numberNode(n.size());case BOOLEAN_TO_STATUS->json.getNodeFactory().textNode(n.asBoolean()?"AVAILABLE":"REMOVED");case ENUM_MAPPING->json.getNodeFactory().textNode(switch(n.asText().toUpperCase()){case "ACTIVE","AVAILABLE"->"AVAILABLE";case "HELD","RESERVED"->"RESERVED";case "SOLD"->"SOLD";default->"REMOVED";});case SEAT_RANGE_TO_QUANTITY->throw new IllegalArgumentException("SEAT_RANGE_TO_QUANTITY requires an approved two-field mapping definition");};}
 public record ValidationResult(boolean valid,List<String> errors,ObjectNode transformedSample){ }
}
