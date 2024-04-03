package com.ys.authentication.domain.core;

import com.ys.authentication.domain.core.AuthenticationInfo;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RecordAuthenticationInfoRedisPort {
    Mono<AuthenticationInfo> save(AuthenticationInfo authenticationInfo);
    Mono<Void> deleteAll(List<AuthenticationInfo> authenticationInfos);
}
