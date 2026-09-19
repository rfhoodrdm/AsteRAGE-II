package com.rfhoodrdm.asterage2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rfhoodrdm.asterage2.entry.AsteRAGE2;

@SpringBootApplication
public class AsteRageIIApplication {
	
	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(AsteRageIIApplication.class, args);
	}

}
