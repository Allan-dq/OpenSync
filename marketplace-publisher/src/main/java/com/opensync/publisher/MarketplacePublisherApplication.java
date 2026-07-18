package com.opensync.publisher;
import com.opensync.persistence.entity.BaseEntity;import com.opensync.persistence.repository.InventoryRepository;import org.springframework.boot.*;import org.springframework.boot.autoconfigure.*;import org.springframework.boot.autoconfigure.domain.EntityScan;import org.springframework.data.jpa.repository.config.EnableJpaRepositories;import org.springframework.retry.annotation.EnableRetry;
@SpringBootApplication @EnableRetry @EntityScan(basePackageClasses=BaseEntity.class) @EnableJpaRepositories(basePackageClasses=InventoryRepository.class)
public class MarketplacePublisherApplication{public static void main(String[] a){SpringApplication.run(MarketplacePublisherApplication.class,a);}}

