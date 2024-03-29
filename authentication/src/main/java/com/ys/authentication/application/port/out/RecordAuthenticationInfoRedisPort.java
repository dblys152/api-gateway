package com.ys.authentication.application.port.out;

import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.AuthenticationInfos;

public interface RecordAuthenticationInfoRedisPort {
    void save(AuthenticationInfo authenticationInfo);
    void deleteAll(AuthenticationInfos authenticationInfos);
}
