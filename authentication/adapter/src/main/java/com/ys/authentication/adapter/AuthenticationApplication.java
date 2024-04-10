package com.ys.authentication.adapter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@ComponentScan(basePackages = "com.ys.authentication")
@EnableCaching
@RestController
public class AuthenticationApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AuthenticationApplication.class)
				.web(WebApplicationType.REACTIVE)
				.run(args);
	}

	@GetMapping("/hello")
	public Mono<String> hello() {
		return Mono.just("hello");
	}
}
