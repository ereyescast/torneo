package com.torneo.copaestudiantil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TorneoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TorneoAppApplication.class, args);}

	@GetMapping
	public String saludar(){
		return "Este recurso maneja una petición de tipo GET";
	}
}
