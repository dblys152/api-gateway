package com.ys.authentication.adapter.out.cache;

import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.fixture.SupportAuthenticationInfoFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
class AuthenticationInfoRedisRepositoryTest extends SupportAuthenticationInfoFixture {
    @Autowired
    private AuthenticationInfoRedisRepository repository;

    private AuthenticationInfoRedisEntity authenticationInfoRedisEntity;

    @BeforeEach
    void setUp() {
        AuthenticationInfo authenticationInfo = AuthenticationInfo.create(USER_ID, REFRESH_TOKEN_INFO, CLIENT_IP);
        authenticationInfoRedisEntity = AuthenticationInfoRedisEntity.fromDomain(authenticationInfo);
    }

    @Test
    void save() {
        AuthenticationInfoRedisEntity actual = repository.save(authenticationInfoRedisEntity);

        assertThat(actual).isNotNull();
    }

    @Test
    void findAllByUserId() {
        AuthenticationInfoRedisEntity saved = repository.save(authenticationInfoRedisEntity);

        List<AuthenticationInfoRedisEntity> actual = repository.findAllByUserId(saved.getUserId());

        assertThat(actual).isNotEmpty();
    }

    @AfterEach
    void rollback() {
        repository.delete(authenticationInfoRedisEntity);
    }
}