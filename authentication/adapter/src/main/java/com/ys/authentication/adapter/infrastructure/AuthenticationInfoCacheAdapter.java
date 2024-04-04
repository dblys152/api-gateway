package com.ys.authentication.adapter.infrastructure;

import com.ys.authentication.domain.core.AuthenticationInfo;
import com.ys.authentication.domain.core.LoadAuthenticationInfoRedisPort;
import com.ys.authentication.domain.core.RecordAuthenticationInfoRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationInfoCacheAdapter implements RecordAuthenticationInfoRedisPort, LoadAuthenticationInfoRedisPort {
    private static final String HASH_NAME = "authentication_info";
    private final ReactiveRedisTemplate<String, AuthenticationInfo> reactiveRedisTemplate;

    @Override
    public Mono<AuthenticationInfo> save(AuthenticationInfo authenticationInfo) {
        String key = HASH_NAME + ":" + authenticationInfo.getUserId();
        return reactiveRedisTemplate.opsForValue()
                .set(key, authenticationInfo)
                .flatMap(result -> reactiveRedisTemplate.expireAt(key, authenticationInfo.getExpiredAt().toInstant()))
                .then(Mono.just(authenticationInfo));
    }

    @Override
    public Mono<Void> deleteAll(List<AuthenticationInfo> authenticationInfoList) {
        return Mono.empty();
//        return repository.deleteAll(authenticationInfoList.stream()
//                .map(AuthenticationInfoRedisEntity::fromDomain)
//                .toList());
    }

    @Override
    public Mono<AuthenticationInfo> findByRefreshToken(String refreshToken) {
        return Mono.empty();
//        Mono<AuthenticationInfoRedisEntity> entityMono = repository.findByRefreshToken(refreshToken)
//                .switchIfEmpty(Mono.error(new NoSuchElementException(ExceptionMessages.NO_DATA_MESSAGE)));
//
//        return entityMono
//                .map(entity -> entity.toDomain());
    }

    @Override
    public Flux<AuthenticationInfo> findAllByUserId(Long userId) {
        return Flux.empty();
//        Flux<AuthenticationInfoRedisEntity> entityFlux = repository.findAllByUserId(userId);
//
//        return entityFlux.map(entity -> entity.toDomain());
    }
}
