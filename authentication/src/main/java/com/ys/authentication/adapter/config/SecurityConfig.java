package com.ys.authentication.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.authentication.adapter.filter.TokenHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final int STRENGTH_OF_256 = 16;

    @Value("${jwt.secret}")
    private String SECRET;

    @Bean
    protected SecurityWebFilterChain webFilterChain(ServerHttpSecurity httpSecurity, ObjectMapper objectMapper) throws Exception {
        httpSecurity
                .httpBasic(withDefaults())
                .formLogin((form) -> form.disable())
                .csrf((csrf) -> csrf.disable())
                .authorizeExchange((exchangeSpec) -> exchangeSpec
                        .pathMatchers("/swagger-ui/**",
                                "hello",
                                "/api/login").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(new TokenHeaderFilter(SECRET, objectMapper), SecurityWebFiltersOrder.AUTHENTICATION);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH_OF_256);
    }
}
