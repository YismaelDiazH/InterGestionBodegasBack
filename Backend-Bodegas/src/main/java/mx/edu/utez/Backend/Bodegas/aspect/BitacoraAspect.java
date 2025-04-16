package mx.edu.utez.Backend.Bodegas.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BitacoraAspect {

    private Object obtenerIdEntidad(Object[] args, Object result) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return arg;
            }
        }
        if (result != null) {
            try {
                return result.getClass().getMethod("getId").invoke(result);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String obtenerDatosRelevantes(Object result) {
        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "Error al serializar datos";
        }
    }
}
