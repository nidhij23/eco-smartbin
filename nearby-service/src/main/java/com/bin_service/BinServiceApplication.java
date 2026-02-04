package com.bin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BinServiceApplication {

	public static void main(String[] args) {
		System.out.println("HELLO WORLD");
		var context = SpringApplication.run(BinServiceApplication.class, args);
		System.out.println("DATABASE URL: " + context.getEnvironment().getProperty("spring.datasource.url"));
	}


}
