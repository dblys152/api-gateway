package com.ys.user.application.port.out;

import com.ys.user.domain.User;
import com.ys.user.domain.UserId;
import com.ys.user.domain.Users;

import java.util.Optional;

public interface LoadUserPort {
    User selectOneByIdAndWithdrawnAtIsNull(UserId userId);
    Users selectAllByEmailAndWithdrawnAtIsNull(String email);
    Users selectAllByMobileAndWithdrawnAtIsNull(String mobile);
}
