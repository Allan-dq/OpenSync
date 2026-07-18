package com.opensync.gateway.ai;
import static org.assertj.core.api.Assertions.*;import com.fasterxml.jackson.databind.ObjectMapper;import com.opensync.contracts.*;import org.junit.jupiter.api.Test;
class MappingValidationServiceTest{
 private final ObjectMapper json=new ObjectMapper();
 @Test void validatesMockPartnerBMapping()throws Exception{var sample=json.readTree("""
 {"external_listing_code":"PB-1","event_code":"E1","seat_location":{"area":"101","line":"A","from":1,"to":4},"number_available":4,"face_value":{"amount":18550,"currency_code":"USD","unit":"CENT"},"inventory_state":"ACTIVE","revision_number":22,"last_modified":"2026-07-16T14:31:00Z"}
 """);var suggestion=new MockLlmClient().suggestMapping(sample,CanonicalSchema.inventory());var result=new MappingValidationService(json).validate(sample,suggestion);assertThat(result.valid()).isTrue();assertThat(result.transformedSample().path("unitPrice").decimalValue()).isEqualByComparingTo("185.5");}
 @Test void rejectsTransformationOutsideAllowList(){var raw="{\"mappings\":[{\"sourcePath\":\"$.x\",\"targetField\":\"unitPrice\",\"transformation\":\"EXECUTE_JAVA\"}],\"confidence\":1,\"warnings\":[]}";assertThatThrownBy(()->json.readValue(raw,SchemaMappingSuggestion.class)).hasMessageContaining("EXECUTE_JAVA");}
}
