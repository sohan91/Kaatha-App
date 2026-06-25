package com.kaatha.gateway.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(
            RouteLocatorBuilder builder) {

        return builder.routes()

                .route("auth-service",
                        r -> r.path("/auth/**")
                                .uri("lb://AUTH-SERVICE"))

                .route("customer-service",
                        r -> r.path("/customers/**")
                                .uri("lb://CUSTOMER-SERVICE"))

                .route("shopkeeper-service",
                        r -> r.path("/shopkeepers/**")
                                .uri("lb://SHOPKEEPER-SERVICE"))

                .route("item-service",
                        r -> r.path("/items/**")
                                .uri("lb://ITEM-SERVICE"))

                .route("transaction-service",
                        r -> r.path("/transactions/**")
                                .uri("lb://TRANSACTION-SERVICE"))

                .route("ledger-service",
                        r -> r.path("/ledgers/**")
                                .uri("lb://LEDGER-SERVICE"))

                .route("notification-service",
                        r -> r.path("/notifications/**")
                                .uri("lb://NOTIFICATION-SERVICE"))

                .build();
    }
}