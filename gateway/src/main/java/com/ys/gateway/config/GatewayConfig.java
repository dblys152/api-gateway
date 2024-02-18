package com.ys.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.gateway.filter.TokenHeaderGlobalFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GatewayConfig {
    @Value("${jwt.secret}")
    private String SECRET;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", route -> route
                        .path("/api/sign-up/**",
                                "/api/users/**")
                        .uri("lb://USER-SERVICE"))
                .build();
    }

    @Bean
    public GlobalFilter tokenHeaderGlobalFilter(ObjectMapper objectMapper) {
        List<String> excludedPaths = List.of(
                "/api/sign-up"
        );
        return new TokenHeaderGlobalFilter(excludedPaths, SECRET, objectMapper);
    }
}
