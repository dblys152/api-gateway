package com.ys.authentication.application.event;

import com.ys.authentication.domain.event.AuthenticationEvent;
import com.ys.shared.event.DomainEvent;
import com.ys.shared.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthenticationEventPublisher implements DomainEventPublisher<AuthenticationEvent> {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(String eventType, AuthenticationEvent payload, LocalDateTime publishedAt) {
        //PayloadInfo payloadInfo = PayloadInfoStore.THREAD_LOCAL.get();
        String publisherId = "SYSTEM";
        eventPublisher.publishEvent(DomainEvent.create(
                eventType, payload, publisherId, publishedAt));
    }
}
