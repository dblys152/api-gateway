package com.ys.authentication.domain.core;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class AuthenticationInfo {
    private static ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    @NotNull
    private Long authenticationInfoId;

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

    public static AuthenticationInfo of(Long authenticationInfoId, Long userId, String refreshToken, Date expiredAt, String clientIp, LocalDateTime createdAt) {
        return new AuthenticationInfo(authenticationInfoId, userId, refreshToken, expiredAt, clientIp, createdAt);
    }

    public static AuthenticationInfo create(Long userId, String refreshToken, Date expiredAt, String clientIp) {
        Long authenticationInfoId = TsidCreator.getTsid256().toLong();
        return new AuthenticationInfo(authenticationInfoId, userId, refreshToken, expiredAt, clientIp, LocalDateTime.now());
    }

    public Duration getExpiredDuration() {
        Instant expiredAtInstant = this.expiredAt.toInstant();
        Instant currentAtInstant = ZonedDateTime.now(KST_ZONE_ID)
                .truncatedTo(ChronoUnit.SECONDS)
                .toInstant();
        return Duration.between(currentAtInstant, expiredAtInstant);
    }
}
