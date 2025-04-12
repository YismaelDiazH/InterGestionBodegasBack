package mx.edu.utez.Backend.Bodegas.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "tu_contraseña_segura";
        System.out.println("Contraseña cifrada: " + encoder.encode(password));
    }
}
