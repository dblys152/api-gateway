package com.ys.authentication.application.port.out;

import com.ys.user.domain.User;

public interface LoadUserPort {
    User selectOneByEmailAndWithdrawnAtIsNull(String email);
}
