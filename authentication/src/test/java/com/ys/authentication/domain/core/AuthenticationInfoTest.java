package com.ys.authentication.domain.core;

import com.ys.shared.jwt.JwtInfo;
import com.ys.user.domain.UserId;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthenticationInfoTest {
    private static final UserId USER_ID = UserId.of(999999999L);
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTcwMzQwNTAsImV4cCI6MTcyODU3MDA1MH0.BLrV10sirnz4wcdv9-MY3rkwa1qehj4pYT8rJbOdtAY";
    private static final Date NOW_DATE = new Date();
    private static final Date EXPIRED_AT = new Date(NOW_DATE.getTime() + 31536000000L);
    private static final JwtInfo REFRESH_TOKEN_INFO = JwtInfo.of(REFRESH_TOKEN, EXPIRED_AT);
    private static final String CLIENT_IP = "127.0.0.1";

    @Test
    void 인증_정보를_생성한다() {
        AuthenticationInfo actual = AuthenticationInfo.create(USER_ID, REFRESH_TOKEN_INFO, CLIENT_IP);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getAuthenticationInfoId()).isNotNull(),
                () -> assertThat(actual.getUserId()).isEqualTo(USER_ID),
                () -> assertThat(actual.getRefreshTokenInfo()).isEqualTo(REFRESH_TOKEN_INFO),
                () -> assertThat(actual.getClientIp()).isEqualTo(CLIENT_IP),
                () -> assertThat(actual.getCreatedAt()).isNotNull()
        );
    }
}
