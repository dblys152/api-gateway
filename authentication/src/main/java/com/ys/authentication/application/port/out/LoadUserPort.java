package com.ys.authentication.application.port.out;

import com.ys.user.domain.User;
import com.ys.user.domain.UserId;

public interface LoadUserPort {
    User selectOneByIdAndWithdrawnAtIsNull(UserId userId);
    User selectOneByEmailAndWithdrawnAtIsNull(String email);
}
