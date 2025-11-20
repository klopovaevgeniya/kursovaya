package com.example.AutoDetail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoDetailApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutoDetailApplication.class, args);
	}
}
