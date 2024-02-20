package com.ys.authentication.adapter.out.persistence;

import com.ys.authentication.adapter.config.MybatisConfig;
import com.ys.authentication.fixture.SupportUserFixture;
import com.ys.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MybatisConfig.class)
class UserRepositoryTest extends SupportUserFixture {
    private static final String AES_SECRET = "Q1RNUy1QUk9ELU9GLUVOQ1JZUFRJT04K";

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of(
                USER_ID,
                UserType.USER,
                UserStatus.JOINED,
                Account.of(EMAIL, PASSWORD, OLD_PASSWORD, PASSWORD_MODIFIED_AT, PASSWORD_WRONG_COUNT, LAST_LOGIN_AT),
                Profile.of(USER_NAME, MOBILE, BIRTH_DATE, Gender.MALE.name()),
                USER_ROLES,
                NOW,
                NOW,
                null
        );
    }

    @Test
    void updateByPasswordWrongCount() {
        repository.updateByPasswordWrongCount(user);
    }

    @Test
    void updateByLastLoginAtAndPasswordWrongCount() {
        repository.updateByLastLoginAtAndPasswordWrongCount(user);
    }

    @Test
    void selectOneByEmailAndWithdrawnAtIsNull() {
        Optional<User> actual = repository.selectOneByEmailAndWithdrawnAtIsNull(EMAIL);

        assertThat(actual).isNotNull();
    }
}