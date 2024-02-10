package com.ys.user.application.port.in;

import com.ys.user.domain.CreateUserCommand;
import com.ys.user.domain.User;

public interface SignUpUseCase {
    User signUp(CreateUserCommand command);
}
