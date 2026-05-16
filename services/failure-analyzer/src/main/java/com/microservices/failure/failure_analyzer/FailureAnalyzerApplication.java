package com.microservices.failure.failure_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class FailureAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FailureAnalyzerApplication.class, args);
	}

}
