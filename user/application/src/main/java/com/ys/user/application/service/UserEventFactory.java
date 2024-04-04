package com.ys.user.application.service;

import com.ys.shared.utils.EventFactory;
import com.ys.user.domain.User;
import com.ys.user.domain.event.UserEvent;
import org.springframework.stereotype.Component;

@Component
public class UserEventFactory implements EventFactory<User, UserEvent> {
    @Override
    public UserEvent create(User domain) {
        return UserEvent.fromDomain(domain);
    }
}
