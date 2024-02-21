package com.ys.authentication.application.port.out;

import com.ys.authentication.domain.AuthenticationInfo;

public interface RecordAuthenticationInfoPort {
    void insert(AuthenticationInfo authenticationInfo);
}
