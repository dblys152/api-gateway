package com.ys.authentication.application.usecase;

import com.ys.authentication.domain.core.TokenInfo;
import reactor.core.publisher.Mono;

public interface RefreshTokenUseCase {
    Mono<TokenInfo> refresh(String refreshToken, String base64Secret);
}
