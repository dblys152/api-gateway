package com.ys.authentication.application.usecase;

import com.ys.shared.jwt.PayloadInfo;
import reactor.core.publisher.Mono;

public interface GetTokenPayloadInfoUseCase {
    Mono<PayloadInfo> get();
}
