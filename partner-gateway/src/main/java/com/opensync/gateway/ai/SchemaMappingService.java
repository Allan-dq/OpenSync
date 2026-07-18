package com.opensync.gateway.ai;
import com.fasterxml.jackson.databind.*;import com.opensync.contracts.*;import com.opensync.gateway.api.GatewayException;import com.opensync.persistence.entity.PartnerSchemaMappingEntity;import com.opensync.persistence.repository.PartnerSchemaMappingRepository;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
@Service
public class SchemaMappingService{
 private final LlmClient llm;private final MappingValidationService validator;private final PartnerSchemaMappingRepository mappings;private final ObjectMapper json;
 public SchemaMappingService(LlmClient l,MappingValidationService v,PartnerSchemaMappingRepository m,ObjectMapper j){llm=l;validator=v;mappings=m;json=j;}
 public Preview preview(JsonNode sample){var suggestion=llm.suggestMapping(sample,CanonicalSchema.inventory());var validation=validator.validate(sample,suggestion);return new Preview(suggestion,validation.valid(),validation.errors(),validation.transformedSample());}
 @Transactional public PartnerSchemaMappingEntity save(String partnerId,JsonNode sample,SchemaMappingSuggestion suggestion,String approvedBy){if(approvedBy==null||approvedBy.isBlank())throw new GatewayException(ErrorCode.MAPPING_VALIDATION_FAILED,"X-Approved-By is required");var result=validator.validate(sample,suggestion);if(!result.valid())throw new GatewayException(ErrorCode.MAPPING_VALIDATION_FAILED,String.join("; ",result.errors()));try{int version=mappings.findFirstByPartnerIdOrderByMappingVersionDesc(partnerId).map(x->x.getMappingVersion()+1).orElse(1);var entity=new PartnerSchemaMappingEntity(partnerId,version,json.writeValueAsString(suggestion),"PENDING");entity.approve(approvedBy);return mappings.save(entity);}catch(Exception e){if(e instanceof GatewayException g)throw g;throw new GatewayException(ErrorCode.INTERNAL_PROCESSING_ERROR,"Could not save schema mapping");}}
 public record Preview(SchemaMappingSuggestion suggestion,boolean valid,java.util.List<String> errors,JsonNode transformedSample){ }
 public record SaveRequest(JsonNode sample,SchemaMappingSuggestion suggestion){ }
}

