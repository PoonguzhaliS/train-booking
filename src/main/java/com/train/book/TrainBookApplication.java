package com.train.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class TrainBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainBookApplication.class, args);
	}

}
