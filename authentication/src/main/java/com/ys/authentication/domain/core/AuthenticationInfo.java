package com.ys.authentication.domain.core;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ys.shared.jwt.JwtInfo;
import com.ys.user.domain.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class AuthenticationInfo {
    @NotNull
    private Long authenticationInfoId;

    @NotNull
    private UserId userId;

    @NotNull
    private JwtInfo refreshTokenInfo;

    @NotNull
    private String clientIp;

    @NotNull
    private LocalDateTime createdAt;

    public static AuthenticationInfo of(Long authenticationInfoId, UserId userId, JwtInfo refreshTokenInfo, String clientIp, LocalDateTime createdAt) {
        return new AuthenticationInfo(authenticationInfoId, userId, refreshTokenInfo, clientIp, createdAt);
    }

    public static AuthenticationInfo create(UserId userId, JwtInfo refreshTokenInfo, String clientIp) {
        Long authenticationInfoId = TsidCreator.getTsid256().toLong();
        return new AuthenticationInfo(authenticationInfoId, userId, refreshTokenInfo, clientIp, LocalDateTime.now());
    }
}
