package com.ys.authentication.adapter.config;

import com.ys.authentication.domain.event.AuthenticationEventType;
import com.ys.shared.queue.RabbitMqExchange;
import com.ys.shared.queue.RabbitMqExchangeNameMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMqConfig {
    private static final String QUORUM_QUEUE_TYPE = "quorum";

    @Value("${rabbitmq.queue.process-login-success}")
    private String PROCESS_LOGIN_SUCCESS_QUEUE;
    @Value("${rabbitmq.queue.process-login-failure}")
    private String PROCESS_LOGIN_FAILURE_QUEUE;
    @Value("${rabbitmq.queue.dead-letter-user}")
    private String DEAD_LETTER_USER_QUEUE;

    @Value("${rabbitmq.exchange.authentication.name}")
    private String AUTHENTICATION_EXCHANGE;
    @Value("${rabbitmq.exchange.authentication.login-success-routing-key}")
    private String AUTHENTICATION_EXCHANGE_LOGIN_SUCCESS_ROUTING_KEY;
    @Value("${rabbitmq.exchange.authentication.login-failure-routing-key}")
    private String AUTHENTICATION_EXCHANGE_LOGIN_FAILURE_ROUTING_KEY;
    @Value("${rabbitmq.exchange.dead-letter.name}")
    private String DEAD_LETTER_EXCHANGE;
    @Value("${rabbitmq.exchange.dead-letter.user-routing-key}")
    private String DEAD_LETTER_EXCHANGE_USER_ROUTING_KEY;

    /* Queue */
    @Bean
    public Queue processLoginSuccessQueue() {
        return makeQueue(PROCESS_LOGIN_SUCCESS_QUEUE, QUORUM_QUEUE_TYPE, 4, DEAD_LETTER_EXCHANGE, DEAD_LETTER_EXCHANGE_USER_ROUTING_KEY);
    }
    @Bean
    public Queue processLoginFailureQueue() {
        return makeQueue(PROCESS_LOGIN_FAILURE_QUEUE, QUORUM_QUEUE_TYPE, 4, DEAD_LETTER_EXCHANGE, DEAD_LETTER_EXCHANGE_USER_ROUTING_KEY);
    }
    @Bean
    public Queue deadLetterUserQueue() {
        return makeQueue(DEAD_LETTER_USER_QUEUE, QUORUM_QUEUE_TYPE, 4);
    }
    private Queue makeQueue(String name, String type, int deliveryLimit, String dlxName, String dlxRoutingKey) {
        return QueueBuilder.durable(name)
                .withArgument("x-queue-type", type)
                .withArgument("x-delivery-limit", deliveryLimit)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", dlxRoutingKey)
                .build();
    }
    private Queue makeQueue(String name, String type, int deliveryLimit) {
        return QueueBuilder.durable(name)
                .withArgument("x-queue-type", type)
                .withArgument("x-delivery-limit", deliveryLimit)
                .build();
    }

    /* Exchange */
    @Bean
    public TopicExchange authenticationExchange() {
        return new TopicExchange(AUTHENTICATION_EXCHANGE);
    }
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     *  Binding
     *  AuthenticationExchange -> [ProcessLoginSuccessQueue, ProcessLoginFailureQueue]
     *  DeadLetterExchange -> [DeadLetterUserQueue]
     */
    @Bean
    public Binding processLoginSuccessQueueToAuthenticationExchangeBinding() {
        return BindingBuilder.bind(processLoginSuccessQueue())
                .to(authenticationExchange())
                .with(AUTHENTICATION_EXCHANGE_LOGIN_SUCCESS_ROUTING_KEY);
    }
    @Bean
    public Binding processLoginFailureQueueToAuthenticationExchangeBinding() {
        return BindingBuilder.bind(processLoginFailureQueue())
                .to(authenticationExchange())
                .with(AUTHENTICATION_EXCHANGE_LOGIN_FAILURE_ROUTING_KEY);
    }
    @Bean
    public Binding deadLetterUserQueueToDeadLetterExchangeBinding() {
        return BindingBuilder.bind(deadLetterUserQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_EXCHANGE_USER_ROUTING_KEY);
    }

    /* Server Connection */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareQueue(processLoginSuccessQueue());
        rabbitAdmin.declareQueue(processLoginFailureQueue());
        rabbitAdmin.declareQueue(deadLetterUserQueue());
        rabbitAdmin.declareExchange(authenticationExchange());
        rabbitAdmin.declareExchange(deadLetterExchange());
        rabbitAdmin.declareBinding(processLoginSuccessQueueToAuthenticationExchangeBinding());
        rabbitAdmin.declareBinding(processLoginFailureQueueToAuthenticationExchangeBinding());
        rabbitAdmin.declareBinding(deadLetterUserQueueToDeadLetterExchangeBinding());
        return rabbitAdmin;
    }

    // Queue Name Mapping For Message Sender
    @Bean
    public RabbitMqExchangeNameMapping rabbitMqExchangeNameMapping() {
        RabbitMqExchange loginSuccessAuthenticationExchange = RabbitMqExchange.of(AUTHENTICATION_EXCHANGE, AUTHENTICATION_EXCHANGE_LOGIN_SUCCESS_ROUTING_KEY);
        RabbitMqExchange loginFailureAuthenticationExchange = RabbitMqExchange.of(AUTHENTICATION_EXCHANGE, AUTHENTICATION_EXCHANGE_LOGIN_FAILURE_ROUTING_KEY);
        RabbitMqExchangeNameMapping mapping = new RabbitMqExchangeNameMapping();
        mapping.add(AuthenticationEventType.LOGIN_SUCCEED_EVENT.name(), loginSuccessAuthenticationExchange);
        mapping.add(AuthenticationEventType.LOGIN_FAILED_EVENT.name(), loginFailureAuthenticationExchange);
        return mapping;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                Message message = correlationData.getReturned().getMessage();
                byte[] body = message.getBody();
                log.error("Fail to produce. ID: {}, Message: {}", correlationData.getId(), body);
            }
        });

        return rabbitTemplate;
    }
}
