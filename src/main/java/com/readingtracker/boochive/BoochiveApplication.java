package com.readingtracker.boochive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class BoochiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoochiveApplication.class, args);
	}

}
