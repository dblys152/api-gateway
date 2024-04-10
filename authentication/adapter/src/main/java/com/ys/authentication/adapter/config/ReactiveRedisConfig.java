package com.ys.authentication.adapter.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ys.authentication.domain.core.AuthenticationInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class ReactiveRedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, AuthenticationInfo> reactiveAuthenticationInfoRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializationContext.RedisSerializationContextBuilder<String, AuthenticationInfo> builder
                = RedisSerializationContext.newSerializationContext(keySerializer);

        Jackson2JsonRedisSerializer<AuthenticationInfo> valueSerializer = new Jackson2JsonRedisSerializer<>(objectMapper(), AuthenticationInfo.class);
        RedisSerializationContext<String, AuthenticationInfo> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveHashOperations<String, String, AuthenticationInfo> reactiveAuthenticationInfoHashOperations(
            ReactiveRedisConnectionFactory factory) {
        return reactiveAuthenticationInfoRedisTemplate(factory).opsForHash();
    }

    @Bean
    public ReactiveValueOperations<String, AuthenticationInfo> reactiveAuthenticationInfoValueOperations(
            ReactiveRedisConnectionFactory factory) {
        return reactiveAuthenticationInfoRedisTemplate(factory).opsForValue();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
