package com.ys.user.application.port.in.model;

import com.ys.user.domain.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpUserRequest {
    @NotNull
    @Size(min = 8, max = 30)
    private String email;

    @NotNull
    @Size(min = 9, max = 15)
    private String password;

    @NotNull
    @Size(min = 1, max = 40)
    private String name;

    @NotNull
    @Size(min = 1, max = 15)
    private String mobile;

    private LocalDate birthDate;

    private Gender gender;
}
