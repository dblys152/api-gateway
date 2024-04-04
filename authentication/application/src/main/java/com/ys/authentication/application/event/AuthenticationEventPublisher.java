package com.ys.authentication.application.event;

import com.ys.authentication.application.service.IdentityProvider;
import com.ys.authentication.domain.event.AuthenticationEvent;
import com.ys.shared.event.DomainEvent;
import com.ys.shared.event.DomainEventPublisher;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.PayloadInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthenticationEventPublisher implements DomainEventPublisher<AuthenticationEvent> {
    private static final String SYSTEM = "SYSTEM";

    private final IdentityProvider identityProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(String eventType, AuthenticationEvent payload, LocalDateTime publishedAt) {
        try {
            PayloadInfo payloadInfo = identityProvider.getPayloadInfo();
            eventPublisher.publishEvent(DomainEvent.create(
                    eventType, payload, String.valueOf(payloadInfo.getUserId()), publishedAt));
        } catch (UnauthorizedException ex) {
            eventPublisher.publishEvent(
                    DomainEvent.create(eventType, payload, SYSTEM, publishedAt));
        }
    }
}
