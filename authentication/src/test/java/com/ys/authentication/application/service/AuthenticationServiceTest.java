package com.ys.authentication.application.service;

import com.ys.authentication.application.port.out.LoadUserPort;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoPort;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.application.port.out.RecordUserPort;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.TokenInfo;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.user.domain.Account;
import com.ys.user.domain.Profile;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String BASE64_SECRET = "TVNDLVVTRVItU0VDUkVULUZPUi1BVVRIT1JJWkFUSU9OLVRFU1Q=";
    private static final String ANY_EMAIL = "test@mail.com";
    private static final String ANY_PASSWORD = "sfestefsetkjklfse";
    private static final String CLIENT_IP = "127.0.0.1";
    private static final UserId ANY_USER_ID = UserId.of(123L);
    private static final String ANY_NAME = "ANY_NAME";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTcwMzQwNTAsImV4cCI6MTcyODU3MDA1MH0.BLrV10sirnz4wcdv9-MY3rkwa1qehj4pYT8rJbOdtAY";

    @InjectMocks
    private AuthenticationService sut;

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private RecordUserPort recordUserPort;
    @Mock
    private RecordAuthenticationInfoPort recordAuthenticationInfoPort;
    @Mock
    private RecordAuthenticationInfoRedisPort recordAuthenticationInfoRedisPort;

    @Test
    void 로그인_한다() {
        User user = getMockUser();
        given(loadUserPort.selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(user);
        given(user.matchesPassword(ANY_PASSWORD)).willReturn(true);

        TokenInfo actual = sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> then(loadUserPort).should().selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(user).should().validateExceededPasswordWrongCount(),
                () -> then(user).should().matchesPassword(ANY_PASSWORD),
                () -> then(user).should().changeLastLoginAtAndInitPasswordWrongCount(),
                () -> then(recordUserPort).should().updateByLastLoginAtAndPasswordWrongCount(user),
                () -> then(recordAuthenticationInfoPort).should().insert(any(AuthenticationInfo.class)),
                () -> then(recordAuthenticationInfoRedisPort).should().save(any(AuthenticationInfo.class))
        );
    }

    private User getMockUser() {
        User user = mock(User.class);
        Account account = mock(Account.class);
        Profile profile = mock(Profile.class);
        given(user.getUserId()).willReturn(ANY_USER_ID);
        given(account.getEmail()).willReturn(ANY_EMAIL);
        given(user.getAccount()).willReturn(account);
        given(profile.getName()).willReturn(ANY_NAME);
        given(user.getProfile()).willReturn(profile);
        return user;
    }

    @Test
    void 로그인_시_비밀번호_틀린_횟수가_초과되었으면_에러를_반환한다() {
        User user = mock(User.class);
        given(loadUserPort.selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(user);
        doThrow(UnauthorizedException.class).when(user).validateExceededPasswordWrongCount();

        assertAll(
                () -> assertThatThrownBy(() -> sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP)).isInstanceOf(UnauthorizedException.class),
                () -> then(loadUserPort).should().selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(user).should().validateExceededPasswordWrongCount()
        );
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_에러를_반환한다() {
        User user = mock(User.class);
        given(loadUserPort.selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(user);
        given(user.matchesPassword(ANY_PASSWORD)).willReturn(false);

        assertAll(
                () -> assertThatThrownBy(() -> sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP)).isInstanceOf(UnauthorizedException.class),
                () -> then(loadUserPort).should().selectOneByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(user).should().validateExceededPasswordWrongCount(),
                () -> then(user).should().matchesPassword(ANY_PASSWORD)
        );
    }
}