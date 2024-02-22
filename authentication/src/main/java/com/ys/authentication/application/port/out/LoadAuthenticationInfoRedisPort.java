package com.ys.authentication.application.port.out;

import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.AuthenticationInfos;

public interface LoadAuthenticationInfoRedisPort {
    AuthenticationInfo findByRefreshToken(String refreshToken);
    AuthenticationInfos findAllByUserId(Long userId);
}
