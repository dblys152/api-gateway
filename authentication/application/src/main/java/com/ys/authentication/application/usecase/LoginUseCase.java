package com.ys.authentication.application.usecase;

import com.ys.authentication.domain.core.TokenInfo;
import reactor.core.publisher.Mono;

public interface LoginUseCase {
    Mono<TokenInfo> login(String email, String password, String base64Secret, String clientIp);
}
