package com.ys.authentication.application.port.out;

import com.ys.user.domain.User;

public interface RecordUserPort {
    void updateByPasswordWrongCount(User user);
    void updateByLastLoginAtAndPasswordWrongCount(User user);
}
