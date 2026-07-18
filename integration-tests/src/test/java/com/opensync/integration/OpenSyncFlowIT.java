package com.opensync.integration;

import static io.restassured.RestAssured.given;import static org.awaitility.Awaitility.await;import static org.hamcrest.Matchers.*;import static org.mockito.Mockito.*;
import com.opensync.gateway.PartnerGatewayApplication;import com.opensync.processor.InventoryProcessorApplication;import com.opensync.publisher.MarketplacePublisherApplication;import com.opensync.publisher.service.*;import com.opensync.persistence.entity.BaseEntity;import com.opensync.persistence.repository.*;
import io.restassured.RestAssured;import java.time.Duration;import org.junit.jupiter.api.*;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.*;import org.springframework.boot.autoconfigure.*;import org.springframework.boot.autoconfigure.domain.EntityScan;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.boot.test.web.server.LocalServerPort;import org.springframework.context.annotation.*;import org.springframework.data.jpa.repository.config.EnableJpaRepositories;import org.springframework.kafka.annotation.EnableKafka;import org.springframework.scheduling.annotation.EnableScheduling;import org.springframework.test.context.DynamicPropertyRegistry;import org.springframework.test.context.DynamicPropertySource;import org.springframework.test.context.bean.override.mockito.MockitoBean;import org.testcontainers.containers.GenericContainer;import org.testcontainers.containers.PostgreSQLContainer;import org.testcontainers.junit.jupiter.Container;import org.testcontainers.junit.jupiter.Testcontainers;import org.testcontainers.kafka.KafkaContainer;import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker=true)
@SpringBootTest(classes=OpenSyncFlowIT.TestApplication.class,webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenSyncFlowIT{
 @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("opensync").withUsername("opensync").withPassword("opensync");
 @Container static KafkaContainer kafka=new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
 @Container static GenericContainer<?> redis=new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
 @DynamicPropertySource static void properties(DynamicPropertyRegistry r){r.add("spring.datasource.url",postgres::getJdbcUrl);r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);r.add("spring.kafka.bootstrap-servers",kafka::getBootstrapServers);r.add("spring.data.redis.host",redis::getHost);r.add("spring.data.redis.port",()->redis.getMappedPort(6379));r.add("opensync.security.admin-api-key",()->"integration-admin-key");}
 @LocalServerPort int port;@MockitoBean MarketplaceClient marketplace;@Autowired InventorySyncJobRepository jobs;@Autowired InventoryRepository inventory;@Autowired DeadLetterMessageRepository dlq;
 @BeforeEach void setup(){RestAssured.port=port;reset(marketplace);}
 @Test void normalDuplicateStaleOutageAndRecovery(){
  var v12=payload(12,"PA-10001");
  var requestId=given().contentType("application/json").header("X-API-Key","partner-a-demo-key").header("Idempotency-Key","idem-normal").body(v12).when().post("/api/v1/partners/partner-a/inventory").then().statusCode(202).body("status",equalTo("ACCEPTED")).extract().path("requestId").toString();
  await().atMost(Duration.ofSeconds(15)).until(()->jobs.findByRequestId(requestId).map(j->j.getStatus().name()).orElse("").equals("PUBLISHED"));
  given().contentType("application/json").header("X-API-Key","partner-a-demo-key").header("Idempotency-Key","idem-normal").body(v12).post("/api/v1/partners/partner-a/inventory").then().statusCode(202).body("requestId",equalTo(requestId));
  var staleId=given().contentType("application/json").header("X-API-Key","partner-a-demo-key").header("Idempotency-Key","idem-stale").body(payload(11,"PA-10001")).post("/api/v1/partners/partner-a/inventory").then().statusCode(202).extract().path("requestId").toString();
  await().atMost(Duration.ofSeconds(10)).until(()->jobs.findByRequestId(staleId).map(j->j.getStatus().name()).orElse("").equals("STALE_REJECTED"));
  doThrow(new MarketplaceUnavailableException("planned outage")).when(marketplace).publish(any());
  var failed=given().contentType("application/json").header("X-API-Key","partner-a-demo-key").header("Idempotency-Key","idem-outage").body(payload(13,"PA-10001")).post("/api/v1/partners/partner-a/inventory").then().statusCode(202).extract().path("requestId").toString();
  await().atMost(Duration.ofSeconds(20)).until(()->jobs.findByRequestId(failed).map(j->j.getStatus().name()).orElse("").equals("DEAD_LETTERED"));
  var message=dlq.findAll().stream().filter(d->"PENDING".equals(d.getStatus())).findFirst().orElseThrow();reset(marketplace);
  given().header("X-Admin-API-Key","integration-admin-key").post("/api/v1/admin/dead-letter/"+message.getMessageId()+"/replay").then().statusCode(202);
  await().atMost(Duration.ofSeconds(15)).until(()->dlq.findById(message.getMessageId()).map(d->"RESOLVED".equals(d.getStatus())).orElse(false));
 }
 private String payload(long v,String id){return "{\"listingId\":\""+id+"\",\"eventId\":\"EVT-1\",\"section\":\"101\",\"row\":\"A\",\"seats\":[1,2],\"price\":50.00,\"currency\":\"USD\",\"available\":true,\"version\":"+v+",\"updatedAt\":\"2026-07-16T14:30:00Z\"}";}
 @SpringBootConfiguration @EnableAutoConfiguration @EnableScheduling @EnableKafka @EntityScan(basePackageClasses=BaseEntity.class) @EnableJpaRepositories(basePackageClasses=InventoryRepository.class)
 @ComponentScan(basePackages="com.opensync",excludeFilters=@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,classes={PartnerGatewayApplication.class,InventoryProcessorApplication.class,MarketplacePublisherApplication.class})) static class TestApplication{}
}
