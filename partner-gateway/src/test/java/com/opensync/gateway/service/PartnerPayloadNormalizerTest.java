package com.opensync.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensync.contracts.InventoryStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PartnerPayloadNormalizerTest {
 private final ObjectMapper json=new ObjectMapper(); private final PartnerPayloadNormalizer normalizer=new PartnerPayloadNormalizer();
 @Test void normalizesPartnerA() throws Exception {
  var p=json.readTree("""
    {"listingId":"PA-1","eventId":"E1","section":"101","row":"A","seats":[1,2,3,4],"price":185.50,"currency":"USD","available":true,"version":12,"updatedAt":"2026-07-16T14:30:00Z"}
    """);
  var i=normalizer.normalize("partner-a",p);
  assertThat(i.partnerListingId()).isEqualTo("PA-1");assertThat(i.quantity()).isEqualTo(4);assertThat(i.status()).isEqualTo(InventoryStatus.AVAILABLE);
 }
 @Test void convertsPartnerBCentsAndSeatRange() throws Exception {
  var p=json.readTree("""
    {"external_listing_code":"PB-1","event_code":"E1","seat_location":{"area":"101","line":"A","from":1,"to":4},"number_available":4,"face_value":{"amount":18550,"currency_code":"USD","unit":"CENT"},"inventory_state":"ACTIVE","revision_number":22,"last_modified":"2026-07-16T14:31:00Z"}
    """);
  var i=normalizer.normalize("partner-b",p);
  assertThat(i.unitPrice()).isEqualByComparingTo(new BigDecimal("185.50"));assertThat(i.quantity()).isEqualTo(4);assertThat(i.section()).isEqualTo("101");
 }
}
