package com.ys.authentication.adapter.infrastructure.persistence;

import com.ys.authentication.adapter.config.R2dbcConfig;
import com.ys.authentication.adapter.infrastructure.fixture.SupportUserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@EnableAutoConfiguration
@ContextConfiguration(classes = R2dbcConfig.class)
class UserReactiveRepositoryTest extends SupportUserFixture {
    private static final String AES_SECRET = "Q1RNUy1QUk9ELU9GLUVOQ1JZUFRJT04K";

    @Autowired
    private UserReactiveRepository repository;

    @Test
    void findByIdAndWithdrawnAtIsNull() {
        Mono<UserEntity> actual = repository.findByIdAndWithdrawnAtIsNull(USER_ID.get());

        StepVerifier.create(actual)
                .verifyComplete();
    }

    @Test
    void findByEmailAndWithdrawnAtIsNull() {
        Mono<UserEntity> actual = repository.findByEmailAndWithdrawnAtIsNull(EMAIL);

        StepVerifier.create(actual)
                        .verifyComplete();
    }

    @Test
    void findById() {
        Mono<UserEntity> actual = repository.findById(USER_ID.get());
        StepVerifier.create(actual)
                .verifyComplete();
    }
}