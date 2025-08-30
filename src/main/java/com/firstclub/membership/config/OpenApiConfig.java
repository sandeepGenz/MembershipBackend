package com.firstclub.membership.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("firstclub-membership")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("FirstClub Membership API")
                        .description("APIs for managing membership plans, tiers, subscriptions and benefits")
                        .version("1.0")))
                .packagesToScan("com.firstclub.membership.controller")
                .build();
    }
}