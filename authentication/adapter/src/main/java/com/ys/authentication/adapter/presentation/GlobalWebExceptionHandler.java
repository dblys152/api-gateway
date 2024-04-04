package com.ys.authentication.adapter.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.shared.exception.AccessDeniedException;
import com.ys.shared.exception.BadRequestException;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.utils.ApiResponseModel;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalWebExceptionHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveHttpStatus(ex);
        return doResponse(status, exchange, ex.getMessage());
    }

    private HttpStatus resolveHttpStatus(Throwable ex) {
        if (ex instanceof BadRequestException ||
                ex instanceof ConstraintViolationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        } else {
            return INTERNAL_SERVER_ERROR;
        }
    }

    private Mono<Void> doResponse(HttpStatus status, ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] errorResponseBytes = objectMapper.writeValueAsBytes(
                    ApiResponseModel.error(status.value(), message));
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(errorResponseBytes)));
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            return doResponseDefaultServerError(exchange);
        }
    }

    private Mono<Void> doResponseDefaultServerError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.setComplete();
    }
}
