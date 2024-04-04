package com.ys.authentication.adapter.config;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.ys.authentication.adapter.infrastructure.persistence")
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    @Value("${spring.r2dbc.url}")
    private String url;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Bean
    @Qualifier("mysql")
    @Override
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                io.r2dbc.postgresql.PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .port(5432)
                        .username(username)
                        .password(password)
                        .database("your_database_name") // Change this to your database name
                        .build()
        );
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator());
        return initializer;
    }
}
