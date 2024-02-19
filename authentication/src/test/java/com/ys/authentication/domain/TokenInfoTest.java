package com.ys.authentication.domain;

import com.ys.infrastructure.jwt.JwtInfo;
import com.ys.infrastructure.jwt.PayloadInfo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TokenInfoTest {
    private static final String BASE64_SECRET = "TVNDLVVTRVItU0VDUkVULUZPUi1BVVRIT1JJWkFUSU9OLVRFU1Q=";
    private static final String ANY_EMAIL = "test@mail.com";
    private static final Long ANY_USER_ID = 123L;
    private static final String ANY_NAME = "ANY_NAME";
    private static final String ANY_ROLES = "ROLE_ADMIN,ROLE_USER";
    private static final PayloadInfo PAYLOAD_INFO = PayloadInfo.of(ANY_EMAIL, ANY_USER_ID, ANY_NAME, ANY_ROLES);
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTcwMzQwNTAsImV4cCI6MTcyODU3MDA1MH0.BLrV10sirnz4wcdv9-MY3rkwa1qehj4pYT8rJbOdtAY";
    private static final Date NOW = new Date();
    private static final JwtInfo REFRESH_TOKEN_INFO = JwtInfo.of(REFRESH_TOKEN, new Date(NOW.getTime() + 31536000000L));

    @Test
    void 토큰_정보를_생성한다() {
        TokenInfo actual = TokenInfo.create(BASE64_SECRET, PAYLOAD_INFO);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTokenType()).isNotNull(),
                () -> assertThat(actual.getAccessTokenInfo()).isNotNull(),
                () -> assertThat(actual.getRefreshTokenInfo()).isNotNull()
        );
    }

    @Test
    void 리프레쉬_토큰으로부터_토큰_정보를_생성한다() {
        TokenInfo actual = TokenInfo.create(BASE64_SECRET, PAYLOAD_INFO, REFRESH_TOKEN_INFO);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTokenType()).isNotNull(),
                () -> assertThat(actual.getAccessTokenInfo()).isNotNull(),
                () -> assertThat(actual.getRefreshTokenInfo()).isNotNull()
        );
    }
}