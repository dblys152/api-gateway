package com.ys.user.adapter.infrastructure.persistence;

import com.ys.user.adapter.config.MybatisConfig;
import com.ys.user.adapter.infrastructure.persistence.fixture.SupportUserFixture;
import com.ys.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Account account = Account.create(SupportUserFixture.EMAIL, SupportUserFixture.PASSWORD);
        Profile profile = Profile.of(SupportUserFixture.USER_NAME, SupportUserFixture.MOBILE, SupportUserFixture.BIRTH_DATE, Gender.MALE.name());
        CreateUserCommand command = new CreateUserCommand(account, profile);
        user = User.create(command);
        user.encrypt(AES_SECRET);
    }

    @Test
    void insert() {
        repository.insert(user);
    }

    @Test
    void updateByPassword() {
        repository.insert(user);
        repository.updateByPassword(user);
    }

    @Test
    void updateByProfile() {
        repository.insert(user);
        repository.updateByProfile(user);
    }

    @Test
    void updateByWithdrawnAt() {
        repository.insert(user);
        repository.updateByWithdrawnAt(user);
    }

    @Test
    void selectOneByIdAndWithdrawnAtIsNull() {
        repository.insert(user);

        Optional<User> actual = repository.selectOneByIdAndWithdrawnAtIsNull(user.getUserId());

        assertThat(actual).isPresent();
    }

    @Test
    void selectAllByEmailAndWithdrawnAtIsNull() {
        repository.insert(user);

        List<User> actual = repository.selectAllByEmailAndWithdrawnAtIsNull(user.getAccount().getEmail());

        assertThat(actual).isNotEmpty();
    };

    @Test
    void selectAllByMobileAndWithdrawnAtIsNull() {
        repository.insert(user);

        List<User> actual = repository.selectAllByMobileAndWithdrawnAtIsNull(user.getProfile().getMobile());

        assertThat(actual).isNotEmpty();
    };
}