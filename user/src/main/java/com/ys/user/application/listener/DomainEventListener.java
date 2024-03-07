package com.ys.user.application.listener;

import com.ys.shared.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventListener {
//    private final RabbitTemplate rabbitTemplate;
//    private final QueueNameMapping<RabbitMqExchange> queueNameMapping;

    @EventListener
    public void on(DomainEvent<?> event) {
//        RabbitMqExchange exchange = queueNameMapping.get(event.getType());
//
//        rabbitTemplate.convertAndSend(
//                exchange.getName(), exchange.getRoutingKey(), event.serialize());

        log.info("Receive DomainEvent name: {} OccurredAt: {}", event.getType(), LocalDateTime.now());
    }
}
