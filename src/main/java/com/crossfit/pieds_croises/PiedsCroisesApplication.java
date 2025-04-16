package com.crossfit.pieds_croises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PiedsCroisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiedsCroisesApplication.class, args);
	}

	@GetMapping("/hello")
	public String sayHello() {
		return "Hello World!";
	}

}
