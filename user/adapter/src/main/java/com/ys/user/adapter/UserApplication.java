package com.ys.user.adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan(basePackages = "com.ys.user")
@RestController
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
