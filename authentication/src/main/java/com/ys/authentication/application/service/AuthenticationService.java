package com.ys.authentication.application.service;

import com.ys.authentication.application.port.in.LoginUseCase;
import com.ys.authentication.application.port.out.LoadUserPort;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoPort;
import com.ys.authentication.application.port.out.RecordAuthenticationInfoRedisPort;
import com.ys.authentication.application.port.out.RecordUserPort;
import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.authentication.domain.TokenInfo;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.jwt.JwtInfo;
import com.ys.infrastructure.jwt.PayloadInfo;
import com.ys.user.domain.Account;
import com.ys.user.domain.Profile;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthenticationService implements LoginUseCase {
    private final LoadUserPort loadUserPort;
    private final RecordUserPort recordUserPort;
    private final RecordAuthenticationInfoPort recordAuthenticationInfoPort;
    private final RecordAuthenticationInfoRedisPort recordAuthenticationInfoRedisPort;

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
}
