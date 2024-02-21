package com.ys.authentication.application.port.out;

import com.ys.authentication.domain.AuthenticationInfo;

public interface RecordAuthenticationInfoRedisPort {
    void save(AuthenticationInfo authenticationInfo);
}
