package com.ys.authentication.application.port.in.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotNull
    String refreshToken;
}
