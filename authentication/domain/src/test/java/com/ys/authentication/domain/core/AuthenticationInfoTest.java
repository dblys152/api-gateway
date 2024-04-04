package com.ys.authentication.domain.core;

import com.ys.authentication.domain.core.fixture.SupportAuthenticationInfoFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthenticationInfoTest extends SupportAuthenticationInfoFixture {
    @Test
    void 인증_정보를_생성한다() {
        AuthenticationInfo actual = AuthenticationInfo.create(USER_ID, REFRESH_TOKEN, EXPIRED_AT, CLIENT_IP);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getAuthenticationInfoId()).isNotNull(),
                () -> assertThat(actual.getUserId()).isEqualTo(USER_ID),
                () -> assertThat(actual.getRefreshToken()).isEqualTo(REFRESH_TOKEN),
                () -> assertThat(actual.getExpiredAt()).isEqualTo(EXPIRED_AT),
                () -> assertThat(actual.getClientIp()).isEqualTo(CLIENT_IP),
                () -> assertThat(actual.getCreatedAt()).isNotNull()
        );
    }
}
