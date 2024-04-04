package com.ys.authentication.adapter.infrastructure.persistence;

import com.ys.authentication.adapter.infrastructure.fixture.SupportUserFixture;
import com.ys.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class UserReactiveRepositoryTest extends SupportUserFixture {
    private static final String AES_SECRET = "Q1RNUy1QUk9ELU9GLUVOQ1JZUFRJT04K";

    @Autowired
    private UserReactiveRepository repository;

    @Test
    void findByIdAndWithdrawnAtIsNull() {
        Mono<User> actual = repository.findByIdAndWithdrawnAtIsNull(SupportUserFixture.USER_ID);

        StepVerifier.create(actual)
                .verifyComplete();
    }

    @Test
    void findByEmailAndWithdrawnAtIsNull() {
        Mono<User> actual = repository.findByEmailAndWithdrawnAtIsNull(SupportUserFixture.EMAIL);

        StepVerifier.create(actual)
                        .verifyComplete();
    }
}