package com.opensync.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;import org.springframework.security.crypto.factory.PasswordEncoderFactories;import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class GatewayConfiguration {
 @Bean PasswordEncoder passwordEncoder(){return PasswordEncoderFactories.createDelegatingPasswordEncoder();}
 @Bean OpenAPI openAPI(){return new OpenAPI().info(new Info().title("OpenSync Partner Gateway API").version("v1").description("Asynchronous partner inventory ingestion and operational queries"));}
}

