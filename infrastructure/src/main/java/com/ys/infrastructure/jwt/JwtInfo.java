package com.ys.infrastructure.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.Date;

@Value(staticConstructor = "of")
public class JwtInfo {
    @NotNull
    String value;
    @NotNull
    Date expiredAt;
}
