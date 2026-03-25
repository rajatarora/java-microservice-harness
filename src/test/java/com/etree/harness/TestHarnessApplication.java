package com.etree.harness;

import org.springframework.boot.SpringApplication;

public class TestHarnessApplication {

	public static void main(String[] args) {
		SpringApplication.from(HarnessApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
