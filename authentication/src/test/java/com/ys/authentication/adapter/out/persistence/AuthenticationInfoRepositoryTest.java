package com.ys.authentication.adapter.out.persistence;

import com.ys.authentication.adapter.config.MybatisConfig;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.fixture.SupportAuthenticationInfoFixture;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MybatisConfig.class)
class AuthenticationInfoRepositoryTest extends SupportAuthenticationInfoFixture {
    @Autowired
    private AuthenticationInfoRepository repository;

    @Test
    void insert() {
        AuthenticationInfo authenticationInfo = AuthenticationInfo.create(USER_ID, REFRESH_TOKEN_INFO, CLIENT_IP);
        repository.insert(authenticationInfo);
    }
}