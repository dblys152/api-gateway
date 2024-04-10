package com.ys.authentication.adapter.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.ys.authentication.adapter")
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    private static final String DATABASE_DRIVER = "postgresql";

    @Value("${r2dbc.host}")
    private String host;
    @Value("${r2dbc.port}")
    private int port;
    @Value("${r2dbc.database}")
    private String database;
    @Value("${r2dbc.username}")
    private String username;
    @Value("${r2dbc.password}")
    private String password;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");

        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .options(options)
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Bean
    public TransactionalOperator transactionalOperator() {
        return TransactionalOperator.create(reactiveTransactionManager());
    }
}
