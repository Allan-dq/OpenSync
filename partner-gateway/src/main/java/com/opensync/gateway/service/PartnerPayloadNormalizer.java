package com.opensync.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.opensync.contracts.*;
import com.opensync.gateway.api.GatewayException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Explicit adapters are the trusted production path; LLM suggestions never execute here. */
@Component
public class PartnerPayloadNormalizer {
 private static final Set<String> CURRENCIES=Set.of("USD","CAD","GBP","EUR");
 public CanonicalInventory normalize(String partnerId,JsonNode p){
  try {
   var result=switch(partnerId){case "partner-a"->partnerA(partnerId,p);case "partner-b"->partnerB(partnerId,p);default->canonical(partnerId,p);};
   validate(result); return result;
  } catch(GatewayException e){throw e;} catch(Exception e){throw new GatewayException(ErrorCode.INVALID_PAYLOAD,"Malformed partner inventory payload");}
 }
 private CanonicalInventory partnerA(String id,JsonNode p){
  var seats=p.path("seats"); if(!seats.isArray()||seats.isEmpty()) throw invalid("seats must contain at least one seat");
  int start=seats.get(0).asInt(),end=seats.get(seats.size()-1).asInt();
  return new CanonicalInventory(id,text(p,"listingId"),text(p,"eventId"),optional(p,"venueId"),text(p,"section"),text(p,"row"),start,end,seats.size(),decimal(p,"price"),text(p,"currency"),p.path("available").asBoolean()?InventoryStatus.AVAILABLE:InventoryStatus.REMOVED,p.path("version").asLong(),Instant.parse(text(p,"updatedAt")));
 }
 private CanonicalInventory partnerB(String id,JsonNode p){
  var seat=p.path("seat_location");var price=p.path("face_value");int start=seat.path("from").asInt(),end=seat.path("to").asInt();
  var amount=new BigDecimal(text(price,"amount")); if("CENT".equalsIgnoreCase(optional(price,"unit"))) amount=amount.divide(BigDecimal.valueOf(100),2,RoundingMode.UNNECESSARY);
  var status=switch(text(p,"inventory_state").toUpperCase()){case "ACTIVE","AVAILABLE"->InventoryStatus.AVAILABLE;case "HELD","RESERVED"->InventoryStatus.RESERVED;case "SOLD"->InventoryStatus.SOLD;default->InventoryStatus.REMOVED;};
  int quantity=p.path("number_available").asInt(end-start+1);
  return new CanonicalInventory(id,text(p,"external_listing_code"),text(p,"event_code"),optional(p,"venue_code"),text(seat,"area"),text(seat,"line"),start,end,quantity,amount,text(price,"currency_code"),status,p.path("revision_number").asLong(),Instant.parse(text(p,"last_modified")));
 }
 private CanonicalInventory canonical(String id,JsonNode p){return new CanonicalInventory(id,text(p,"partnerListingId"),text(p,"eventId"),optional(p,"venueId"),text(p,"section"),text(p,"row"),p.path("seatStart").asInt(),p.path("seatEnd").asInt(),p.path("quantity").asInt(),decimal(p,"unitPrice"),text(p,"currency"),InventoryStatus.valueOf(text(p,"status")),p.path("version").asLong(),Instant.parse(text(p,"partnerUpdatedAt")));}
 private void validate(CanonicalInventory i){
  if(i.quantity()<0)throw invalid("quantity must not be negative");
  if(i.seatStart()!=null&&i.seatEnd()!=null&&i.seatEnd()<i.seatStart())throw new GatewayException(ErrorCode.INVALID_SEAT_RANGE,"seatEnd must be greater than or equal to seatStart");
  if(!CURRENCIES.contains(i.currency()))throw new GatewayException(ErrorCode.UNSUPPORTED_CURRENCY,"Unsupported currency: "+i.currency());
 }
 private String text(JsonNode p,String f){if(p.path(f).isMissingNode()||p.path(f).isNull()||p.path(f).asText().isBlank())throw new GatewayException(ErrorCode.MISSING_REQUIRED_FIELD,"Missing required field: "+f);return p.path(f).asText();}
 private String optional(JsonNode p,String f){return p.path(f).isMissingNode()||p.path(f).isNull()?null:p.path(f).asText();}
 private BigDecimal decimal(JsonNode p,String f){return new BigDecimal(text(p,f));}
 private GatewayException invalid(String m){return new GatewayException(ErrorCode.INVALID_PAYLOAD,m);}
}

