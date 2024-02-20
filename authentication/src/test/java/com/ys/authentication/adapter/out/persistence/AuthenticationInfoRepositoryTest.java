package com.ys.authentication.adapter.out.persistence;

import com.ys.authentication.adapter.config.MybatisConfig;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.infrastructure.jwt.JwtInfo;
import com.ys.user.domain.UserId;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MybatisConfig.class)
class AuthenticationInfoRepositoryTest {
    private static final UserId USER_ID = UserId.of(123L);
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTcwMzQwNTAsImV4cCI6MTcyODU3MDA1MH0.BLrV10sirnz4wcdv9-MY3rkwa1qehj4pYT8rJbOdtAY";
    private static final Date NOW_DATE = new Date();
    private static final Date EXPIRED_AT = new Date(NOW_DATE.getTime() + 31536000000L);
    private static final JwtInfo REFRESH_TOKEN_INFO = JwtInfo.of(REFRESH_TOKEN, EXPIRED_AT);
    private static final String CLIENT_IP = "127.0.0.1";

    @Autowired
    private AuthenticationInfoRepository repository;

    @Test
    void insert() {
        AuthenticationInfo authenticationInfo = AuthenticationInfo.create(USER_ID, REFRESH_TOKEN_INFO, CLIENT_IP);
        repository.insert(authenticationInfo);
    }
}