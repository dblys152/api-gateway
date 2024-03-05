package com.ys.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.gateway.filter.TokenHeaderGlobalFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

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
                .route("authentication-service-limit", route -> route
                        .path("/api/login/**")
                        .filters(f -> f.requestRateLimiter(r -> r.setRateLimiter(redisRateLimiter())))
                        .uri("lb://AUTHENTICATION-SERVICE"))
                .route("authentication-service", route -> route
                        .path("/api/token/**",
                                "/api/token-info/**",
                                "/api/logout/**")
                        .uri("lb://AUTHENTICATION-SERVICE"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(20, 40);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    @Bean
    public GlobalFilter tokenHeaderGlobalFilter(ObjectMapper objectMapper) {
        List<String> excludedPaths = List.of(
                "/api/sign-up",
                "/api/login",
                "/api/token"
        );
        return new TokenHeaderGlobalFilter(excludedPaths, SECRET, objectMapper);
    }
}
