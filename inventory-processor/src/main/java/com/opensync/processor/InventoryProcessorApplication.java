package com.opensync.processor;
import com.opensync.persistence.entity.BaseEntity;import com.opensync.persistence.repository.InventoryRepository;
import org.springframework.boot.*;import org.springframework.boot.autoconfigure.*;import org.springframework.boot.autoconfigure.domain.EntityScan;import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication @EntityScan(basePackageClasses=BaseEntity.class) @EnableJpaRepositories(basePackageClasses=InventoryRepository.class)
public class InventoryProcessorApplication{public static void main(String[] a){SpringApplication.run(InventoryProcessorApplication.class,a);}}

