package com.dev.cloud_connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudConnectApplication.class, args);
	}

}
