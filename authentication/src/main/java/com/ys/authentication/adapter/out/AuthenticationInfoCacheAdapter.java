package com.ys.authentication.adapter.out;

import com.ys.authentication.adapter.out.cache.AuthenticationInfoRedisEntity;
import com.ys.authentication.adapter.out.cache.AuthenticationInfoRedisRepository;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.domain.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationInfoCacheAdapter implements RecordAuthenticationInfoRedisPort {
    private final AuthenticationInfoRedisRepository repository;

    @Override
    public void save(AuthenticationInfo authenticationInfo) {
        repository.save(AuthenticationInfoRedisEntity.fromDomain(authenticationInfo));
    }
}
