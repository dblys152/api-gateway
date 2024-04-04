package com.ys.user.application.port.in.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeUserPasswordRequest {
    @NotNull
    @Size(min = 1, max = 255)
    private String password;
}
