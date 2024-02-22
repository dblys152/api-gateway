package com.ys.authentication.adapter.in.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ys.infrastructure.jwt.PayloadInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenPayloadModel {
    String email;

    Long userId;

    String name;

    String roles;

    String token;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    Date expiredAt;

    public static TokenPayloadModel from(PayloadInfo payloadInfo) {
        return new TokenPayloadModel(
                payloadInfo.getEmail(),
                payloadInfo.getUserId(),
                payloadInfo.getName(),
                payloadInfo.getRoles(),
                payloadInfo.getAccessToken(),
                payloadInfo.getExpiredAt()
        );
    }
}
