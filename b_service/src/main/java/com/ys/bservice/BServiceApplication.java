package com.ys.bservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.ys.bservice")
public class BServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BServiceApplication.class, args);
	}

}
