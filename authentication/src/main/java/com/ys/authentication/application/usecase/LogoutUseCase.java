package com.ys.authentication.application.usecase;

import reactor.core.publisher.Mono;

public interface LogoutUseCase {
    Mono<Void> logout();
}
