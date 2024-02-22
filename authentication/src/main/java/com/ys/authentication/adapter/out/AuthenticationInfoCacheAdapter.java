package com.ys.authentication.adapter.out;

import com.ys.authentication.adapter.out.cache.AuthenticationInfoRedisEntity;
import com.ys.authentication.adapter.out.cache.AuthenticationInfoRedisRepository;
import com.ys.authentication.application.port.out.LoadAuthenticationInfoRedisPort;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.AuthenticationInfos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class AuthenticationInfoCacheAdapter implements RecordAuthenticationInfoRedisPort, LoadAuthenticationInfoRedisPort {
    private final AuthenticationInfoRedisRepository repository;

    @Override
    public void save(AuthenticationInfo authenticationInfo) {
        repository.save(AuthenticationInfoRedisEntity.fromDomain(authenticationInfo));
    }

    @Override
    public void deleteAll(AuthenticationInfos authenticationInfos) {
        repository.deleteAll(authenticationInfos.getItems().stream()
                .map(AuthenticationInfoRedisEntity::fromDomain)
                .toList());
    }

    @Override
    public AuthenticationInfo findByRefreshToken(String refreshToken) {
        return repository.findByRefreshToken(refreshToken)
                .orElseThrow(NoSuchElementException::new)
                .toDomain();
    }

    @Override
    public AuthenticationInfos findAllByUserId(Long userId) {
        List<AuthenticationInfoRedisEntity> entityList = repository.findAllByUserId(userId);
        return AuthenticationInfos.of(entityList.stream()
                .map(e -> e.toDomain())
                .toList());
    }
}
