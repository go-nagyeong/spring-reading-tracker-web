package com.readingtracker.boochive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BoochiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoochiveApplication.class, args);
	}

}
