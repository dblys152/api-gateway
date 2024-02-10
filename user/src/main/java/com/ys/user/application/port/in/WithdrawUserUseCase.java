package com.ys.user.application.port.in;

import com.ys.user.domain.UserId;

public interface WithdrawUserUseCase {
    void withdraw(UserId userId);
}
