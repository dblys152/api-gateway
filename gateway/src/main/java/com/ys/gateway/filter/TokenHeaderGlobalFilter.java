package com.ys.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.infrastructure.exception.BadRequestException;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.jwt.JwtProvider;
import com.ys.infrastructure.utils.ApiResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TokenHeaderGlobalFilter implements GlobalFilter {
    private static final String TOKEN_TYPE = "Bearer";

    private final List<String> excludedPaths;
    private final String secret;
    private final ObjectMapper objectMapper;

    public TokenHeaderGlobalFilter(List<String> excludedPaths, String secret, ObjectMapper objectMapper) {
        this.excludedPaths = excludedPaths;
        this.secret = secret;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();
        if (excludedPaths.stream().anyMatch(requestPath::matches)) {
            return chain.filter(exchange);
        }

        try {
            String accessToken = resolveToken(exchange);

            JwtProvider.getInstance().getPayload(accessToken, secret);

            return chain.filter(exchange);
        } catch (BadRequestException ex) {
            return handleException(exchange, HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (UnauthorizedException ex) {
            return handleException(exchange, HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            return handleException(exchange, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private String resolveToken(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        List<String> authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        String authorizationValue = Optional.ofNullable(authorizationHeader)
                .orElse(Collections.emptyList())
                .stream()
                .filter(h -> StringUtils.isNotEmpty(h))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No authorization"));

        return authorizationValue.replace(TOKEN_TYPE + " ", "");
    }

    private Mono<Void> handleException(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        log.error(message);
        try {
            String responseBody = objectMapper.writeValueAsString(
                    ApiResponseModel.error(status.value(), message));

            return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody.getBytes())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
