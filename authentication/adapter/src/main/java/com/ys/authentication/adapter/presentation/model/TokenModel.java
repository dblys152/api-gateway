package com.ys.authentication.adapter.presentation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ys.authentication.domain.core.TokenInfo;
import com.ys.shared.jwt.JwtInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenModel {
    String tokenType;

    String accessToken;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    Date accessTokenExpiredAt;

    String refreshToken;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    Date refreshTokenExpiredAt;

    public static TokenModel fromDomain(TokenInfo tokenInfo) {
        JwtInfo accessTokenInfo = tokenInfo.getAccessTokenInfo();
        JwtInfo refreshTokenInfo = tokenInfo.getRefreshTokenInfo();
        return new TokenModel(
                tokenInfo.getTokenType(),
                accessTokenInfo.getValue(),
                accessTokenInfo.getExpiredAt(),
                refreshTokenInfo.getValue(),
                refreshTokenInfo.getExpiredAt()
        );
    }
}
