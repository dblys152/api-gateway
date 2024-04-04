package com.ys.authentication.domain.core;

import com.ys.user.domain.UserId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoadAuthenticationInfoRedisPort {
    Mono<AuthenticationInfo> findByRefreshToken(String refreshToken);
    Flux<AuthenticationInfo> findAllByUserId(UserId userId);
}
