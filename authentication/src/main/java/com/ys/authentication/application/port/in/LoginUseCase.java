package com.ys.authentication.application.port.in;

import com.ys.authentication.domain.TokenInfo;

public interface LoginUseCase {
    TokenInfo login(String email, String password, String base64Secret, String clientIp);
}
