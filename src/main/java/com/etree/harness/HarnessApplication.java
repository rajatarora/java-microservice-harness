package com.etree.harness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Harness Spring Boot application.
 * <p>
 * This class boots the Spring context and enables JPA auditing used across
 * the application.
 */
@EnableJpaAuditing
@SpringBootApplication
public class HarnessApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 *
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HarnessApplication.class, args);
	}

}
