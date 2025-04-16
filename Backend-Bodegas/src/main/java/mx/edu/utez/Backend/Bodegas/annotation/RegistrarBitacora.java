package mx.edu.utez.Backend.Bodegas.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegistrarBitacora {
    String value();  // Ej: "CREAR", "ELIMINAR"
    String entidad(); // Ej: "Bodega", "Renta"
}