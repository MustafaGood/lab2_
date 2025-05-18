package com.mustafa.laboration2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Detta är huvudfilen för programmet
// @SpringBootApplication visar att detta är ett Spring Boot-program
@SpringBootApplication
public class Laboration2Application {

	// Detta är huvudmetoden som startar programmet
	// args är en lista med kommandoradsargument som kan skickas till programmet
	public static void main(String[] args) {
		// Startar Spring Boot-programmet
		SpringApplication.run(Laboration2Application.class, args);
	}

}
