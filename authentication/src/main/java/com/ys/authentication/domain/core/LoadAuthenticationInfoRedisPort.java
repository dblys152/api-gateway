package com.ys.authentication.domain.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoadAuthenticationInfoRedisPort {
    Mono<AuthenticationInfo> findByRefreshToken(String refreshToken);
    Flux<AuthenticationInfo> findAllByUserId(Long userId);
}
