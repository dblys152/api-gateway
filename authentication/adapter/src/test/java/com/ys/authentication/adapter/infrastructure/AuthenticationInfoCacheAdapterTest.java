package com.ys.authentication.adapter.infrastructure;

import com.ys.authentication.adapter.infrastructure.fixture.SupportAuthenticationInfoFixture;
import com.ys.authentication.domain.core.AuthenticationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AuthenticationInfoCacheAdapterTest extends SupportAuthenticationInfoFixture {
    private static final String HASH_NAME = "AUTHENTICATION_INFO";

    @Autowired
    private ReactiveValueOperations<String, AuthenticationInfo> reactiveValueOperations;

    private AuthenticationInfo authenticationInfo;

    @BeforeEach
    void setUp() {
        authenticationInfo = AuthenticationInfo.of(AUTHENTICATION_INFO_ID, USER_ID, REFRESH_TOKEN, EXPIRED_AT, CLIENT_IP, NOW);
    }

    @Test
    void write() {
        String key = HASH_NAME + ":" + USER_ID;

        Mono<Boolean> actual = reactiveValueOperations.set(key, authenticationInfo);

        StepVerifier.create(actual)
                .verifyComplete();
    }
}