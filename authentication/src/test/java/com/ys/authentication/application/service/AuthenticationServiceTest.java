package com.ys.authentication.application.service;

import com.ys.authentication.domain.core.AuthenticationInfo;
import com.ys.authentication.domain.core.LoadAuthenticationInfoRedisPort;
import com.ys.authentication.domain.core.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.domain.core.TokenInfo;
import com.ys.authentication.domain.event.AuthenticationEvent;
import com.ys.authentication.domain.event.AuthenticationEventType;
import com.ys.authentication.domain.user.LoadUserPort;
import com.ys.shared.event.DomainEventPublisher;
import com.ys.shared.exception.AccessDeniedException;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.JwtProvider;
import com.ys.shared.jwt.PayloadInfo;
import com.ys.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
    private RecordAuthenticationInfoRedisPort recordAuthenticationInfoRedisPort;
    @Mock
    private LoadAuthenticationInfoRedisPort loadAuthenticationInfoRedisPort;
    @Mock
    private DomainEventPublisher<AuthenticationEvent> domainEventPublisher;

    @BeforeEach
    void setUp() {
        PayloadInfo payloadInfo = PayloadInfo.of(ANY_EMAIL, ANY_USER_ID.get(), ANY_NAME, UserRole.ROLE_USER.name());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(payloadInfo, null));
    }

    @Test
    void 로그인_성공_후_이벤트를_발행한다() {
        User user = getMockUser();
        given(loadUserPort.findByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(Mono.just(user));
        given(user.matchesPassword(ANY_PASSWORD)).willReturn(true);
        given(recordAuthenticationInfoRedisPort.save(any(AuthenticationInfo.class))).willReturn(Mono.just(mock(AuthenticationInfo.class)));

        Mono<TokenInfo> actual = sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP);

        StepVerifier.create(actual)
                .assertNext(tokenInfo -> {
                    assertAll(
                            () -> assertThat(tokenInfo).isNotNull(),
                            () -> then(loadUserPort).should().findByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                            () -> then(user).should().validateExceededPasswordWrongCount(),
                            () -> then(user).should().matchesPassword(ANY_PASSWORD),
                            () -> then(recordAuthenticationInfoRedisPort).should().save(any(AuthenticationInfo.class)),
                            () -> then(domainEventPublisher).should().publish(
                                    eq(AuthenticationEventType.LOGIN_SUCCEED_EVENT.name()), any(AuthenticationEvent.class), any(LocalDateTime.class))
                    );
                })
                .verifyComplete();
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
        given(loadUserPort.findByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(Mono.just(user));
        doThrow(AccessDeniedException.class).when(user).validateExceededPasswordWrongCount();

        Mono<TokenInfo> actual = sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP);

        assertAll(
                () -> StepVerifier.create(actual)
                        .expectError(AccessDeniedException.class)
                        .verify(),
                () -> then(loadUserPort).should().findByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(user).should().validateExceededPasswordWrongCount()
        );
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_이벤트_발행_후_에러를_반환한다() {
        User user = mock(User.class);
        given(user.getUserId()).willReturn(ANY_USER_ID);
        given(loadUserPort.findByEmailAndWithdrawnAtIsNull(ANY_EMAIL)).willReturn(Mono.just(user));
        given(user.matchesPassword(ANY_PASSWORD)).willReturn(false);

        Mono<TokenInfo> actual = sut.login(ANY_EMAIL, ANY_PASSWORD, BASE64_SECRET, CLIENT_IP);

        assertAll(
                () -> StepVerifier.create(actual)
                        .expectError(UnauthorizedException.class)
                        .verify(),
                () -> then(loadUserPort).should().findByEmailAndWithdrawnAtIsNull(ANY_EMAIL),
                () -> then(user).should().validateExceededPasswordWrongCount(),
                () -> then(domainEventPublisher).should().publish(
                        eq(AuthenticationEventType.LOGIN_FAILED_EVENT.name()), any(AuthenticationEvent.class), any(LocalDateTime.class))
        );
    }

    @Test
    void 토큰을_갱신한다() {
        AuthenticationInfo authenticationInfo = mock(AuthenticationInfo.class);
        given(authenticationInfo.getUserId()).willReturn(ANY_USER_ID);
        given(loadAuthenticationInfoRedisPort.findByRefreshToken(REFRESH_TOKEN)).willReturn(Mono.just(authenticationInfo));
        User user = getMockUser();
        given(loadUserPort.findByIdAndWithdrawnAtIsNull(ANY_USER_ID)).willReturn(Mono.just(user));

        Mono<TokenInfo> actual = sut.refresh(REFRESH_TOKEN, BASE64_SECRET);

        StepVerifier.create(actual)
                .assertNext(tokenInfo -> {
                    assertAll(
                            () -> assertThat(tokenInfo).isNotNull(),
                            () -> then(loadAuthenticationInfoRedisPort).should().findByRefreshToken(REFRESH_TOKEN),
                            () -> then(loadUserPort).should().findByIdAndWithdrawnAtIsNull(ANY_USER_ID)
                    );
                })
                .verifyComplete();
    }

    @Test
    void 토큰_갱신_시_REFRESH_TOKEN_정보가_유효하지_않으면_에러를_반환한다() {
        AuthenticationInfo authenticationInfo = mock(AuthenticationInfo.class);
        given(loadAuthenticationInfoRedisPort.findByRefreshToken(REFRESH_TOKEN)).willReturn(Mono.just(authenticationInfo));

        try(MockedStatic<PayloadInfo> mockedStatic = mockStatic(PayloadInfo.class)) {
            mockedStatic.when(() -> JwtProvider.getInstance().getPayload(REFRESH_TOKEN, BASE64_SECRET)).thenThrow(UnauthorizedException.class);

            Mono<TokenInfo> actual = sut.refresh(REFRESH_TOKEN, BASE64_SECRET);

            assertAll(
                    () -> StepVerifier.create(actual)
                            .expectError(UnauthorizedException.class)
                            .verify(),
                    () -> then(loadAuthenticationInfoRedisPort).should().findByRefreshToken(REFRESH_TOKEN)
            );
        }
    }

    @Test
    void 토큰_갱신_시_인증정보의_유저가_존재하지_않으면_에러를_반환한다() {
        AuthenticationInfo authenticationInfo = mock(AuthenticationInfo.class);
        given(authenticationInfo.getUserId()).willReturn(ANY_USER_ID);
        given(loadAuthenticationInfoRedisPort.findByRefreshToken(REFRESH_TOKEN)).willReturn(Mono.just(authenticationInfo));
        given(loadUserPort.findByIdAndWithdrawnAtIsNull(ANY_USER_ID)).willThrow(NoSuchElementException.class);

        Mono<TokenInfo> actual = sut.refresh(REFRESH_TOKEN, BASE64_SECRET);

        assertAll(
                () -> StepVerifier.create(actual)
                        .expectError(UnauthorizedException.class)
                        .verify(),
                () -> then(loadAuthenticationInfoRedisPort).should().findByRefreshToken(REFRESH_TOKEN),
                () -> then(loadUserPort).should().findByIdAndWithdrawnAtIsNull(ANY_USER_ID)
        );
    }

    @Test
    void 토큰_페이로드를_조회한다() {
        Flux<AuthenticationInfo> authenticationInfoFlux = Flux.just(mock(AuthenticationInfo.class));
        given(loadAuthenticationInfoRedisPort.findAllByUserId(ANY_USER_ID)).willReturn(authenticationInfoFlux);

        Mono<PayloadInfo> actual = sut.get();

        StepVerifier.create(actual)
                .assertNext(payloadInfo -> {
                    assertThat(payloadInfo).isNotNull();
                    then(loadAuthenticationInfoRedisPort).should().findAllByUserId(ANY_USER_ID);
                })
                .verifyComplete();
    }

    @Test
    void 토큰_페이로드_조회_시_인증정보가_없으면_에러를_반환한다() {
        given(loadAuthenticationInfoRedisPort.findAllByUserId(ANY_USER_ID)).willReturn(Flux.empty());

        Mono<PayloadInfo> actual = sut.get();

        StepVerifier.create(actual)
                .expectError(UnauthorizedException.class)
                .verify();
        then(loadAuthenticationInfoRedisPort).should().findAllByUserId(ANY_USER_ID);
    }

    @Test
    void 로그아웃_한다() {
        List<AuthenticationInfo> authenticationInfoList = List.of(mock(AuthenticationInfo.class));
        Flux<AuthenticationInfo> authenticationInfoFlux = Flux.fromIterable(authenticationInfoList);
        given(loadAuthenticationInfoRedisPort.findAllByUserId(ANY_USER_ID)).willReturn(authenticationInfoFlux);
        given(recordAuthenticationInfoRedisPort.deleteAll(authenticationInfoList)).willReturn(Mono.empty());

        Mono<Void> actual = sut.logout();

        StepVerifier.create(actual)
                .verifyComplete();

        then(loadAuthenticationInfoRedisPort).should().findAllByUserId(ANY_USER_ID);
        then(recordAuthenticationInfoRedisPort).should().deleteAll(authenticationInfoList);
    }
}