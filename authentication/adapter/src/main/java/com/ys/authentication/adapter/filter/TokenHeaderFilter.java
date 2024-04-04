package com.ys.authentication.adapter.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.shared.exception.BadRequestException;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.JwtProvider;
import com.ys.shared.utils.ApiResponseModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class TokenHeaderFilter implements WebFilter {
    private static final String BEARER_TOKEN_TYPE = "Bearer ";
    private final String secret;
    private final ObjectMapper objectMapper;

    public TokenHeaderFilter(String secret, ObjectMapper objectMapper) {
        this.secret = secret;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return resolveToken(exchange)
                .flatMap(accessToken -> Mono.fromCallable(() -> JwtProvider.getInstance().getPayload(accessToken, secret))
                                .flatMap(payloadInfo -> {
                                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(payloadInfo, null));
                                    return chain.filter(exchange);
                                }))
                .onErrorResume(BadRequestException.class, ex -> handleException(exchange, HttpStatus.BAD_REQUEST, ex.getMessage()))
                .onErrorResume(UnauthorizedException.class, ex -> handleException(exchange, HttpStatus.UNAUTHORIZED, ex.getMessage()))
                .onErrorResume(Exception.class, ex -> handleException(exchange, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    private Mono<String> resolveToken(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .switchIfEmpty(Mono.error(new BadRequestException("No authorization")))
                .map(authorizationValue -> {
                    if (!authorizationValue.startsWith(BEARER_TOKEN_TYPE)) {
                        throw new BadRequestException("Invalid authorization header");
                    }
                    return authorizationValue.substring(BEARER_TOKEN_TYPE.length());
                });
    }

    private Mono<Void> handleException(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] errorResponseBytes = objectMapper.writeValueAsBytes(
                    ApiResponseModel.error(status.value(), message));
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(errorResponseBytes)));
        }  catch (JsonProcessingException e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }
}
