package mx.edu.utez.Backend.Bodegas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BackendBodegasApplication {
	public static void main(String[] args){
		SpringApplication.run(BackendBodegasApplication.class, args);
	}
}