package com.kaatha.shopkeeper.shopkeeper_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class ShopkeeperServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopkeeperServiceApplication.class, args);
	}

}
