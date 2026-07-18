package com.opensync.contracts;
import java.util.*;
public record CanonicalSchema(List<Field> fields){
 public record Field(String name,String type,boolean required){ }
 public static CanonicalSchema inventory(){return new CanonicalSchema(List.of(new Field("partnerListingId","string",true),new Field("eventId","string",true),new Field("venueId","string",false),new Field("section","string",true),new Field("row","string",true),new Field("seatStart","integer",false),new Field("seatEnd","integer",false),new Field("quantity","integer",true),new Field("unitPrice","decimal",true),new Field("currency","string",true),new Field("status","enum",true),new Field("version","integer",true),new Field("partnerUpdatedAt","timestamp",true)));}
}
