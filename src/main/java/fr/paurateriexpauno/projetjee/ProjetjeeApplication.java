package fr.paurateriexpauno.projetjee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan("com.bde.entity")
public class ProjetjeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetjeeApplication.class, args);
	}

}
