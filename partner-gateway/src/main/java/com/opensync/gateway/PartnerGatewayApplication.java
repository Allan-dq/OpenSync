package com.opensync.gateway;

import com.opensync.persistence.entity.BaseEntity;
import com.opensync.persistence.repository.InventoryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackageClasses = BaseEntity.class)
@EnableJpaRepositories(basePackageClasses = InventoryRepository.class)
public class PartnerGatewayApplication {
    public static void main(String[] args) { SpringApplication.run(PartnerGatewayApplication.class, args); }
}

