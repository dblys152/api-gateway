package com.ys.user.application.port.in.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeUserProfileRequest {
    @NotNull
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 128)
    private String mobile;

    @Size(min = 1, max = 128)
    private String birthDate;

    @Size(min = 1, max = 128)
    private String gender;
}
