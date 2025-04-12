package mx.edu.utez.Backend.Bodegas.errors;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {
    @GetMapping("/custom")
    public String throwCustomException() {
        throw new CustomException(
                "Error de negocio controlado",
                "ERR-NEGOCIO",
                "Validación fallida: no se pudo completar la operación"
        );
    }

    @GetMapping("/general")
    public String throwGeneralException() {
        throw new RuntimeException("Ocurrió un error inesperado");
    }
}
