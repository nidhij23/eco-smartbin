package com.smartbin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartBinServiceApplication {

	public static void main(String[] args) {
		System.out.println("HELLO WORLD");
		var context = SpringApplication.run(SmartBinServiceApplication.class, args);
		System.out.println("DATABASE URL: " + context.getEnvironment().getProperty("spring.datasource.url"));
	}


}
