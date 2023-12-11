package com.ys.infrastructure.jwt;

import com.fasterxml.uuid.Generators;
import com.ys.infrastructure.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtProvider {
    private static ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private static JwtProvider instance;

    public static JwtProvider getInstance() {
        if (instance == null) {
            instance = new JwtProvider();
        }
        return instance;
    }

    public JwtInfo generateToken(String secret, long validityMilliseconds, PayloadInfo payloadInfo) {
        Date issuedAt = Date.from(ZonedDateTime.now(KST_ZONE_ID)
                .truncatedTo(ChronoUnit.SECONDS)
                .toInstant());
        Date expirationDate = new Date(issuedAt.getTime() + validityMilliseconds);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        String jwt = Jwts.builder()
                .subject(payloadInfo.getEmail())
                .claim("requestId", Generators.timeBasedEpochGenerator().generate().toString())
                .claim("userId", payloadInfo.getUserId())
                .claim("name", payloadInfo.getName())
                .claim("roles", payloadInfo.getRoles())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(key)
                .compact();

        return JwtInfo.of(jwt, expirationDate);
    }

    public PayloadInfo getPayload(String jwt, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            if (claims == null) {
                throw new UnauthorizedException("No JWT payload");
            }

            validateExpiredAt(claims);

            return PayloadInfo.of(
                    claims.getSubject(),
                    ((Integer) claims.get("userId")),
                    String.valueOf(claims.get("name")),
                    String.valueOf(claims.get("roles")),
                    jwt,
                    claims.getExpiration()
            );
        } catch (UnauthorizedException | JwtException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
    }

    private void validateExpiredAt(Claims claims) {
        Date ktcNow = Date.from(ZonedDateTime.now(KST_ZONE_ID)
                .truncatedTo(ChronoUnit.SECONDS)
                .toInstant());

        if (ktcNow.after(claims.getExpiration())) {
            throw new UnauthorizedException("JWT has expired");
        }
    }
}
