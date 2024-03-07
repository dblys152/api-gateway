package com.ys.authentication.domain;

import com.ys.shared.jwt.JwtInfo;
import com.ys.shared.jwt.JwtProvider;
import com.ys.shared.jwt.PayloadInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class TokenInfo {
    private static final long accessTokenValidityMilliseconds = 3600000L;
    private static final long refreshTokenValidityMilliseconds = 31536000000L;
    private static final String TOKEN_TYPE = "Bearer";

    private String tokenType;
    private JwtInfo accessTokenInfo;
    private JwtInfo refreshTokenInfo;

    public static TokenInfo create(String base64Secret, PayloadInfo payloadInfo) {
        JwtProvider jwtProvider = JwtProvider.getInstance();
        JwtInfo accessTokenInfo = jwtProvider.generateToken(
                base64Secret, accessTokenValidityMilliseconds, payloadInfo);
        JwtInfo refreshTokenInfo = jwtProvider.generateToken(
                base64Secret, refreshTokenValidityMilliseconds, payloadInfo);

        return new TokenInfo(TOKEN_TYPE, accessTokenInfo, refreshTokenInfo);
    }

    public static TokenInfo create(String base64Secret, PayloadInfo payloadInfo, JwtInfo refreshTokenInfo) {
        JwtProvider jwtProvider = JwtProvider.getInstance();
        JwtInfo accessTokenInfo = jwtProvider.generateToken(
                base64Secret, accessTokenValidityMilliseconds, payloadInfo);

        return new TokenInfo(TOKEN_TYPE, accessTokenInfo, refreshTokenInfo);
    }
}
