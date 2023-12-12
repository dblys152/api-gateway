package com.ys.user.domain;

import com.ys.infrastructure.utils.SelfValidating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateUserCommand extends SelfValidating<CreateUserCommand> {
    @Valid
    @NotNull
    Account account;

    @Valid
    @NotNull
    Profile profile;

    public CreateUserCommand(Account account, Profile profile) {
        this.account = account;
        this.profile = profile;
        validateSelf();
    }
}
