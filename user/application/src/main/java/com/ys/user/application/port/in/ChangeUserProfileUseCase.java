package com.ys.user.application.port.in;

import com.ys.user.domain.ChangeUserProfileCommand;
import com.ys.user.domain.User;
import com.ys.user.domain.UserId;

public interface ChangeUserProfileUseCase {
    User change(UserId userId, ChangeUserProfileCommand command);
}
