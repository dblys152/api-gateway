package com.ys.authentication.application.port.in;

import com.ys.infrastructure.jwt.PayloadInfo;

public interface GetTokenPayloadInfoUseCase {
    PayloadInfo get();
}
