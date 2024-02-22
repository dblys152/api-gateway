package com.ys.authentication.application.port.in.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    @Size(min = 1, max = 30)
    private String email;

    @NotNull
    @Size(min = 9, max = 15)
    private String password;
}
