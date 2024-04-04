package com.ys.authentication.domain.core;

import reactor.core.publisher.Mono;

import java.util.List;

public interface RecordAuthenticationInfoRedisPort {
    Mono<AuthenticationInfo> save(AuthenticationInfo authenticationInfo);
    Mono<Void> deleteAll(List<AuthenticationInfo> authenticationInfos);
}
