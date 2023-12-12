package com.ys.user.domain;

import com.ys.infrastructure.utils.SelfValidating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeUserProfileCommand extends SelfValidating<ChangeUserProfileCommand> {
    @Valid
    @NotNull
    Profile profile;

    public ChangeUserProfileCommand(Profile profile) {
        this.profile = profile;
        validateSelf();
    }
}
