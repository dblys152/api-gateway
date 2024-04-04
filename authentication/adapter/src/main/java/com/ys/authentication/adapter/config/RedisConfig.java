package com.ys.authentication.adapter.config;

import com.ys.authentication.domain.core.AuthenticationInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {
    @Bean
    public ReactiveValueOperations<String, AuthenticationInfo> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<AuthenticationInfo> valueSerializer = new Jackson2JsonRedisSerializer<>(AuthenticationInfo.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, AuthenticationInfo> builder = RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, AuthenticationInfo> context = builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context).opsForValue();
    }
}
