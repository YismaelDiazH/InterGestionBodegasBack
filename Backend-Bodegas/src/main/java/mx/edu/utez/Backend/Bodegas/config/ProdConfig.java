package mx.edu.utez.Backend.Bodegas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProdConfig {
    public ProdConfig() {
        System.out.println("Modo Producción: Configuración activa");
    }
}
