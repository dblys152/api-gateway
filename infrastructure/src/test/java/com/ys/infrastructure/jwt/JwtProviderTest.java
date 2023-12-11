package com.ys.infrastructure.jwt;

import com.ys.infrastructure.exception.UnauthorizedException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {
    private static final String BASE64_SECRET = "TVNDLVVTRVItU0VDUkVULUZPUi1BVVRIT1JJWkFUSU9OLVRFU1Q=";
    private static final long ONE_HOUR_VALIDITY_MILLISECONDS = 3600000L;
    private static final Integer ANY_USER_ID = 123;
    private static final String ANY_EMAIL = "test@mail.com";
    private static final String ANY_NAME = "ANY_NAME";
    private static final String ANY_ROLES = "ROLE_ADMIN,ROLE_USER";
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTg0MzU1NTYsImV4cCI6MTY5ODQzOTE1Nn0.JMZbe_t2qDTGXz9XM_Ryj0cB3rbFi_Sh9osPrQxG5fQ";
    private PayloadInfo PAYLOAD_INFO = PayloadInfo.of(ANY_EMAIL, ANY_USER_ID, ANY_NAME, ANY_ROLES, ACCESS_TOKEN, Date.valueOf(LocalDate.now()));

    private JwtProvider sut;
    private JwtInfo jwtInfo;

    @BeforeEach
    void setUp() {
        sut = JwtProvider.getInstance();
        jwtInfo = sut.generateToken(BASE64_SECRET, ONE_HOUR_VALIDITY_MILLISECONDS, PAYLOAD_INFO);
    }

    @Test
    void JWT를_생성한다() {
//        String PRODUCTION_BASE64_SECRET = "xxx";
//        Long HUNDRED_YEAR_MILLISECONDS = 3153600000000L;
//        PayloadInfo payloadInfo = PayloadInfo.of("externalsystem", 3, "외부 시스템", "ROLE_ADMIN");
//        JwtInfo actual = sut.generateToken(PRODUCTION_BASE64_SECRET, HUNDRED_YEAR_MILLISECONDS, payloadInfo);

        JwtInfo actual = sut.generateToken(BASE64_SECRET, ONE_HOUR_VALIDITY_MILLISECONDS, PAYLOAD_INFO);

        assertThat(actual).isNotNull();
        System.out.println(actual.getValue());
    }

    @Test
    void JWT_PAYLOAD를_조회한다() {
        PayloadInfo actual = sut.getPayload(jwtInfo.getValue(), BASE64_SECRET);

        assertAll(
                () -> assertThat(actual.getEmail()).isNotNull(),
                () -> assertThat(actual.getUserId()).isNotNull(),
                () -> assertThat(actual.getName()).isNotNull(),
                () -> assertThat(actual.getRoles()).isNotNull(),
                () -> assertThat(actual.getAccessToken()).isEqualTo(jwtInfo.getValue()),
                () -> assertThat(actual.getExpiredAt()).isEqualTo(jwtInfo.getExpiredAt())
        );
    }

    @Test
    void JWT_PAYLOAD_조회_시_토큰이_유효하지_않으면_에러를_반환한다() {
        String dummyJwt = "eyJhbGciOiJIUzI1NiJ9";
        assertThatThrownBy(() -> sut.getPayload(dummyJwt, BASE64_SECRET)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void Secret이_Base64가_아니면_에러를_반환한다() {
        String notBase64Secret = "notBase64Secret";
        assertThatThrownBy(() -> sut.generateToken(notBase64Secret, ONE_HOUR_VALIDITY_MILLISECONDS, PAYLOAD_INFO)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> sut.getPayload(jwtInfo.getValue(), notBase64Secret)).isInstanceOf(JwtException.class);
    }
}