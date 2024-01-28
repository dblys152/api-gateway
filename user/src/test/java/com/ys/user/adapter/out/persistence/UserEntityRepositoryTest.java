package com.ys.user.adapter.out.persistence;

import com.ys.user.adapter.config.MybatisConfig;
import com.ys.user.domain.*;
import com.ys.user.fixture.SupportUserFixture;
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
class UserEntityRepositoryTest extends SupportUserFixture {
    @Autowired
    private UserEntityRepository repository;

    private static final String AES_SECRET = "Q1RNUy1QUk9ELU9GLUVOQ1JZUFRJT04K";

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        Account account = Account.create(EMAIL, PASSWORD);
        Profile profile = Profile.of(USER_NAME, MOBILE, BIRTH_DATE, Gender.MALE.name());
        CreateUserCommand command = new CreateUserCommand(account, profile);
        userEntity = UserEntity.create(USER_ID, command);
        userEntity.encrypt(AES_SECRET);
    }

    @Test
    void insert() {
        repository.insert(userEntity);
    }

    @Test
    void updateByPasswordWrongCount() {
        repository.insert(userEntity);
        repository.updateByPasswordWrongCount(userEntity);
    }

    @Test
    void updateByLastLoginAtAndPasswordWrongCount() {
        repository.insert(userEntity);
        repository.updateByLastLoginAtAndPasswordWrongCount(userEntity);
    }

    @Test
    void updateByPassword() {
        repository.insert(userEntity);
        repository.updateByPassword(userEntity);
    }

    @Test
    void updateByProfile() {
        repository.insert(userEntity);
        repository.updateByProfile(userEntity);
    }

    @Test
    void updateByWithdrawnAt() {
        repository.insert(userEntity);
        repository.updateByWithdrawnAt(userEntity);
    }

    @Test
    void selectOneById() {
        repository.insert(userEntity);

        Optional<UserEntity> actual = repository.selectOneById(userEntity.getUserId());

        assertThat(actual).isPresent();
    }

    @Test
    void selectOneByIdAndWithdrawnAtIsNull() {
        repository.insert(userEntity);

        Optional<UserEntity> actual = repository.selectOneByIdAndWithdrawnAtIsNull(userEntity.getUserId());

        assertThat(actual).isPresent();
    }

    @Test
    void selectOneByEmail() {
        repository.insert(userEntity);

        Optional<UserEntity> actual = repository.selectOneByEmail(userEntity.getAccount().getEmail());

        assertThat(actual).isPresent();
    };
}