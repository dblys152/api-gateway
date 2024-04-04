package com.ys.authentication.application.service;

import com.ys.authentication.application.usecase.GetTokenPayloadInfoUseCase;
import com.ys.authentication.application.usecase.LoginUseCase;
import com.ys.authentication.application.usecase.LogoutUseCase;
import com.ys.authentication.application.usecase.RefreshTokenUseCase;
import com.ys.authentication.domain.core.AuthenticationInfo;
import com.ys.authentication.domain.core.LoadAuthenticationInfoRedisPort;
import com.ys.authentication.domain.core.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.domain.core.TokenInfo;
import com.ys.authentication.domain.event.AuthenticationEvent;
import com.ys.authentication.domain.event.AuthenticationEventType;
import com.ys.authentication.domain.user.LoadUserPort;
import com.ys.shared.event.DomainEventPublisher;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.JwtInfo;
import com.ys.shared.jwt.JwtProvider;
import com.ys.shared.jwt.PayloadInfo;
import com.ys.user.domain.Account;
import com.ys.user.domain.Profile;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthenticationService implements
        LoginUseCase, RefreshTokenUseCase, GetTokenPayloadInfoUseCase, LogoutUseCase {
    private final LoadUserPort loadUserPort;
    private final RecordAuthenticationInfoRedisPort recordAuthenticationInfoRedisPort;
    private final LoadAuthenticationInfoRedisPort loadAuthenticationInfoRedisPort;
    private final DomainEventPublisher<AuthenticationEvent> domainEventPublisher;
    private final IdentityProvider identityProvider;

    @Override
    public Mono<TokenInfo> login(String email, String password, String base64Secret, String clientIp) {
        return loadUserPort.findByEmailAndWithdrawnAtIsNull(email)
                .flatMap(user -> {
                    user.validateExceededPasswordWrongCount();

                    if (!user.matchesPassword(password)) {
                        return handleUnsuccessfulLogin(user);
                    }

                    return handleSuccessfulLogin(user, base64Secret, clientIp);
                });
    }

    private Mono<TokenInfo> handleUnsuccessfulLogin(User user) {
        return publishAuthenticationEvent(AuthenticationEventType.LOGIN_FAILED_EVENT, user)
                .then(Mono.error(new UnauthorizedException("사용자 정보가 일치하지 않습니다.")));
    }

    private Mono<TokenInfo> handleSuccessfulLogin(User user, String base64Secret, String clientIp) {
        return getTokenInfo(user, base64Secret, null)
                .flatMap(tokenInfo -> {
                    JwtInfo refreshTokenInfo = tokenInfo.getRefreshTokenInfo();
                    AuthenticationInfo authenticationInfo = AuthenticationInfo.create(
                            user.getUserId().get(), refreshTokenInfo.getValue(), refreshTokenInfo.getExpiredAt(), clientIp);
                    return recordAuthenticationInfoRedisPort.save(authenticationInfo)
                            .flatMap(saved -> publishAuthenticationEvent(AuthenticationEventType.LOGIN_SUCCEED_EVENT, user))
                            .thenReturn(tokenInfo);
                });
    }

    private Mono<Void> publishAuthenticationEvent(AuthenticationEventType eventType, User user) {
        return Mono.fromRunnable(() -> domainEventPublisher.publish(eventType.name(), AuthenticationEvent.fromDomain(user), LocalDateTime.now()));
    }

    private Mono<TokenInfo> getTokenInfo(User user, String base64Secret, JwtInfo refreshTokenInfo) {
        return Mono.fromCallable(() -> {
            Account account = user.getAccount();
            Profile profile = user.getProfile();
            PayloadInfo payloadInfo = PayloadInfo.of(
                    account.getEmail(), user.getUserId().get(), profile.getName(), user.getRoles());

            if (refreshTokenInfo == null) {
                return TokenInfo.create(base64Secret, payloadInfo);
            }

            return TokenInfo.create(base64Secret, payloadInfo, refreshTokenInfo);
        });
    }

    @Override
    public Mono<TokenInfo> refresh(String refreshToken, String base64Secret) {
        Mono<AuthenticationInfo> authenticationInfoMono = loadAuthenticationInfoRedisPort.findByRefreshToken(refreshToken);

        Mono<PayloadInfo> payloadInfoMono = Mono.fromCallable(() -> JwtProvider.getInstance().getPayload(refreshToken, base64Secret))
                .onErrorMap(ex -> new UnauthorizedException("토큰이 유효하지 않습니다."));

        return Mono.zip(authenticationInfoMono, payloadInfoMono)
                .flatMap(tuple -> {
                    AuthenticationInfo authenticationInfo = tuple.getT1();
                    return loadUserPort.findByIdAndWithdrawnAtIsNull(UserId.of(authenticationInfo.getUserId()))
                            .flatMap(user -> getTokenInfo(
                                    user, base64Secret, JwtInfo.of(authenticationInfo.getRefreshToken(), authenticationInfo.getExpiredAt())));
                })
                .onErrorResume(NoSuchElementException.class, ex -> Mono.error(new UnauthorizedException("토큰이 유효하지 않습니다.")));
    }

    @Override
    public Mono<PayloadInfo> get() {
        return Mono.fromCallable(() -> identityProvider.getPayloadInfo())
                .flatMap(payloadInfo -> loadAuthenticationInfoRedisPort.findAllByUserId(payloadInfo.getUserId())
                        .switchIfEmpty(Mono.error(new UnauthorizedException("토큰이 유효하지 않습니다.")))
                        .then(Mono.just(payloadInfo)));
    }

    @Override
    public Mono<Void> logout() {
        return Mono.fromCallable(() -> identityProvider.getPayloadInfo())
                .flatMap(payloadInfo -> loadAuthenticationInfoRedisPort.findAllByUserId(payloadInfo.getUserId())
                        .switchIfEmpty(Mono.empty())
                        .collectList()
                        .flatMap(authenticationInfoList -> recordAuthenticationInfoRedisPort.deleteAll(authenticationInfoList)));
    }
}
