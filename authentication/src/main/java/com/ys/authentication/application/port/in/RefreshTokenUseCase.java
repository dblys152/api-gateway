package com.ys.authentication.application.port.in;

import com.ys.authentication.domain.TokenInfo;

public interface RefreshTokenUseCase {
    TokenInfo refresh(String refreshToken, String base64Secret);
}
