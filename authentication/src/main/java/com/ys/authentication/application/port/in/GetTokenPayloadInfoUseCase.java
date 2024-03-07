package com.ys.authentication.application.port.in;

import com.ys.shared.jwt.PayloadInfo;

public interface GetTokenPayloadInfoUseCase {
    PayloadInfo get();
}
