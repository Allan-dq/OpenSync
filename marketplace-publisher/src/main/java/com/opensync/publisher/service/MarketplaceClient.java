package com.opensync.publisher.service;
import com.opensync.contracts.CanonicalInventory;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Component;import org.springframework.web.client.RestClient;
@Component
public class MarketplaceClient{
 private final RestClient client;
 public MarketplaceClient(RestClient.Builder builder,@Value("${opensync.marketplace.base-url:http://localhost:8083}") String url){client=builder.baseUrl(url).build();}
 public void publish(CanonicalInventory i){try{client.post().uri("/marketplace/v1/listings").body(i).retrieve().toBodilessEntity();}catch(Exception e){throw new MarketplaceUnavailableException("Marketplace request failed",e);}}
}

