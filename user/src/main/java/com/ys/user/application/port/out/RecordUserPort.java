package com.ys.user.application.port.out;

import com.ys.user.domain.User;

public interface RecordUserPort {
    void insert(User user);
    void updateByPassword(User user);
    void updateByProfile(User user);
    void updateByWithdrawnAt(User user);
}
