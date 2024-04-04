package com.ys.authentication.application.usecase.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotNull
    String refreshToken;
}
