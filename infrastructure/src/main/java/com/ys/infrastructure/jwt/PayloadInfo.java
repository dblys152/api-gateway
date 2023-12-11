package com.ys.infrastructure.jwt;

import lombok.Value;

import java.util.Date;

@Value(staticConstructor = "of")
public class PayloadInfo {
    String email;
    Integer userId;
    String name;
    String roles;
    String accessToken;
    Date expiredAt;

    public static PayloadInfo of(String email, Integer userId, String name, String roles) {
        return PayloadInfo.of(email, userId, name, roles, null, null);
    }
}
