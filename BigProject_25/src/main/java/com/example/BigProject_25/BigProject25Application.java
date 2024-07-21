package com.example.BigProject_25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/*20240710-2)*/
@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class BigProject25Application {

	public static void main(String[] args) {
		SpringApplication.run(BigProject25Application.class, args);
	}

}