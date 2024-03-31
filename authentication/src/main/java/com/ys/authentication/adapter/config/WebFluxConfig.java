package com.ys.authentication.adapter.config;

import com.ys.authentication.adapter.aspect.ClientIpArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ClientIpArgumentResolver());
        WebFluxConfigurer.super
                .configureArgumentResolvers(configurer);
    }
}
