package com.ys.authentication.application.service;

import com.ys.authentication.application.port.in.GetTokenPayloadInfoUseCase;
import com.ys.authentication.application.port.in.LoginUseCase;
import com.ys.authentication.application.port.in.LogoutUseCase;
import com.ys.authentication.application.port.in.RefreshTokenUseCase;
import com.ys.authentication.application.port.out.*;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.AuthenticationInfos;
import com.ys.authentication.domain.TokenInfo;
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

import java.util.NoSuchElementException;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthenticationService implements
        LoginUseCase, RefreshTokenUseCase, GetTokenPayloadInfoUseCase, LogoutUseCase {
    private final LoadUserPort loadUserPort;
    private final RecordUserPort recordUserPort;
    private final RecordAuthenticationInfoPort recordAuthenticationInfoPort;
    private final RecordAuthenticationInfoRedisPort recordAuthenticationInfoRedisPort;
    private final LoadAuthenticationInfoRedisPort loadAuthenticationInfoRedisPort;

    @Override
    public TokenInfo login(String email, String password, String base64Secret, String clientIp) {
        User user = loadUserPort.selectOneByEmailAndWithdrawnAtIsNull(email);

        user.validateExceededPasswordWrongCount();

        if (!user.matchesPassword(password)) {
            handleUnsuccessfulLogin(user);
        }

        return handleSuccessfulLogin(user, base64Secret, clientIp);
    }

    private void handleUnsuccessfulLogin(User user) {
        user.increasePasswordWrongCount();
        recordUserPort.updateByPasswordWrongCount(user);

        throw new UnauthorizedException("사용자 정보가 일치하지 않습니다.");
    }

    private TokenInfo handleSuccessfulLogin(User user, String base64Secret, String clientIp) {
        changeLastLoginAtAndInitPasswordWrongCount(user);

        TokenInfo tokenInfo = getTokenInfo(user, base64Secret);

        saveAuthenticationInfo(user.getUserId(), tokenInfo.getRefreshTokenInfo(), clientIp);

        return tokenInfo;
    }

    private void changeLastLoginAtAndInitPasswordWrongCount(User user) {
        user.changeLastLoginAtAndInitPasswordWrongCount();
        recordUserPort.updateByLastLoginAtAndPasswordWrongCount(user);
    }

    private TokenInfo getTokenInfo(User user, String base64Secret) {
        Account account = user.getAccount();
        Profile profile = user.getProfile();
        TokenInfo tokenInfo = TokenInfo.create(
                base64Secret,
                PayloadInfo.of(
                        account.getEmail(), user.getUserId().get(), profile.getName(), user.getRoles())
        );
        return tokenInfo;
    }

    private void saveAuthenticationInfo(UserId userId, JwtInfo refreshTokenInfo, String clientIp) {
        AuthenticationInfo authenticationInfo = AuthenticationInfo.create(userId, refreshTokenInfo, clientIp);
        recordAuthenticationInfoPort.insert(authenticationInfo);
        recordAuthenticationInfoRedisPort.save(authenticationInfo);
    }

    @Override
    public TokenInfo refresh(String refreshToken, String base64Secret) {
        try {
            AuthenticationInfo authenticationInfo = loadAuthenticationInfoRedisPort.findByRefreshToken(refreshToken);

            JwtProvider.getInstance().getPayload(refreshToken, base64Secret);

            User user = loadUserPort.selectOneByIdAndWithdrawnAtIsNull(authenticationInfo.getUserId());

            return getTokenInfo(authenticationInfo.getRefreshTokenInfo(), user, base64Secret);
        } catch (NoSuchElementException | UnauthorizedException ex) {
            throw new UnauthorizedException("토큰이 유효하지 않습니다.");
        }
    }

    private TokenInfo getTokenInfo(JwtInfo refreshTokenInfo, User user, String base64Secret) {
        Account account = user.getAccount();
        Profile profile = user.getProfile();
        TokenInfo tokenInfo = TokenInfo.create(
                base64Secret,
                PayloadInfo.of(
                        account.getEmail(), user.getUserId().get(), profile.getName(), user.getRoles()),
                refreshTokenInfo
        );
        return tokenInfo;
    }

    @Override
    public PayloadInfo get() {
        PayloadInfo payloadInfo = User.getPayloadInfo();
        AuthenticationInfos authenticationInfos = loadAuthenticationInfoRedisPort.findAllByUserId(payloadInfo.getUserId());
        if (authenticationInfos.isEmpty()) {
            throw new UnauthorizedException("토큰이 유효하지 않습니다.");
        }
        return payloadInfo;
    }

    @Override
    public void logout() {
        PayloadInfo payloadInfo = User.getPayloadInfo();
        AuthenticationInfos authenticationInfos = loadAuthenticationInfoRedisPort.findAllByUserId(payloadInfo.getUserId());
        if (!authenticationInfos.isEmpty()) {
            recordAuthenticationInfoRedisPort.deleteAll(authenticationInfos);
        }
    }
}
