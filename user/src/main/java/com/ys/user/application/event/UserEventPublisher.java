package com.ys.user.application.event;

import com.ys.infrastructure.event.DomainEvent;
import com.ys.infrastructure.event.DomainEventPublisher;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.jwt.PayloadInfo;
import com.ys.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserEventPublisher implements DomainEventPublisher<User> {
    private static final String SYSTEM = "SYSTEM";

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(String eventType, User payload, LocalDateTime publishedAt) {
        try {
            PayloadInfo payloadInfo = User.getPayloadInfo();
            eventPublisher.publishEvent(DomainEvent.create(
                    eventType, payload, String.valueOf(payloadInfo.getUserId()), publishedAt));
        } catch (UnauthorizedException ex) {
            eventPublisher.publishEvent(
                    DomainEvent.create(eventType, payload, SYSTEM, publishedAt));
        }
    }
}
