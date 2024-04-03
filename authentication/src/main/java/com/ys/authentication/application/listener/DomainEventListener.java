package com.ys.authentication.application.listener;

import com.ys.shared.event.DomainEvent;
import com.ys.shared.queue.QueueNameMapping;
import com.ys.shared.queue.RabbitMqExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventListener {
    private final RabbitTemplate rabbitTemplate;
    private final QueueNameMapping<RabbitMqExchange> queueNameMapping;

    @EventListener
    public void on(DomainEvent<?> event) {
        log.info("Received DomainEvent name: {} OccurredAt: {}", event.getType(), LocalDateTime.now());

        RabbitMqExchange exchange = queueNameMapping.get(event.getType());

        if (exchange == null) {
            log.error("Failed Send Message");
        }

        rabbitTemplate.convertAndSend(
                exchange.getName(),
                exchange.getRoutingKey(),
                new Message(event.serialize().getBytes()),
                new CorrelationData(event.getId().toString()));
    }
}
