package com.ys.authentication.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class AuthenticationInfos {
    @Valid
    @NotNull
    List<AuthenticationInfo> items;

    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }
}
