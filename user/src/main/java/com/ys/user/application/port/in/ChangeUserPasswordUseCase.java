package com.ys.user.application.port.in;

import com.ys.user.domain.UserId;

public interface ChangeUserPasswordUseCase {
    void change(UserId userId, String password);
}
