package com.ys.user.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.user.adapter.filter.TokenHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final int STRENGTH_OF_256 = 16;

    @Value("${jwt.secret}")
    private String SECRET;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/api/sign-up",
                "/api/login",
                "/api/token",
                "/api/temporary-password",
                "/api/user-temp-encrypt"
        );
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity, ObjectMapper objectMapper) throws Exception {
        httpSecurity
                .httpBasic(withDefaults())
                .formLogin((form) -> form.disable())
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                .addFilterBefore(new TokenHeaderFilter(SECRET, objectMapper), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH_OF_256);
    }
}
