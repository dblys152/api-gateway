package com.ys.authentication.domain.event;

import com.ys.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationEvent {
    Long userId;

    public static AuthenticationEvent fromDomain(User user) {
        return new AuthenticationEvent(
                user.getUserId().get()
        );
    }
}
