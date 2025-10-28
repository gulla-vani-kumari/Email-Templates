package com.net.sphuta_tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SphutaTmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(SphutaTmsApplication.class, args);
	}

}
