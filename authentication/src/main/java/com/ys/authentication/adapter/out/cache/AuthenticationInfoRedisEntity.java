package com.ys.authentication.adapter.out.cache;

import com.ys.authentication.domain.AuthenticationInfo;
import com.ys.infrastructure.jwt.JwtInfo;
import com.ys.user.domain.UserId;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Date;

@RedisHash("authentication_info")
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class AuthenticationInfoRedisEntity {
    @Id
    private Long id;

    @Indexed
    @NotNull
    private Long userId;

    @NotNull
    private String refreshToken;

    @NotNull
    private Date expiredAt;

    @NotNull
    private String clientIp;

    @NotNull
    private LocalDateTime createdAt;

    @TimeToLive
    private long ttl;

    public static AuthenticationInfoRedisEntity fromDomain(AuthenticationInfo authenticationInfo) {
        JwtInfo refreshTokenInfo = authenticationInfo.getRefreshTokenInfo();
        return new AuthenticationInfoRedisEntity(
                authenticationInfo.getAuthenticationInfoId(),
                authenticationInfo.getUserId().get(),
                refreshTokenInfo.getValue(),
                refreshTokenInfo.getExpiredAt(),
                authenticationInfo.getClientIp(),
                authenticationInfo.getCreatedAt(),
                calculateTimeoutSeconds(refreshTokenInfo.getExpiredAt())
        );
    }

    private static long calculateTimeoutSeconds(Date expirationTime) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = expirationTime.getTime();
        long timeoutMillis = expirationTimeMillis - currentTimeMillis;
        return Math.max(0, timeoutMillis / 1000);
    }

    public AuthenticationInfo toDomain() {
        return AuthenticationInfo.of(
                this.id,
                UserId.of(this.userId),
                JwtInfo.of(this.refreshToken, this.expiredAt),
                this.clientIp,
                this.createdAt
        );
    }
}
