package mx.edu.utez.Backend.Bodegas.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import mx.edu.utez.Backend.Bodegas.models.bitacora.BitacoraBean;
import mx.edu.utez.Backend.Bodegas.repositories.BitacoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BitacoraService {
    private final BitacoraRepository bitacoraRepository;

    @Autowired
    public BitacoraService(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
    }

    public void registrarAccion(String accion, String entidad, Long idEntidad,
                                Object datos, String usuario) {
        BitacoraBean registro = new BitacoraBean();
        registro.setAccion(accion);
        registro.setEntidadAfectada(entidad);
        registro.setIdEntidad(idEntidad);
        registro.setDetalles(convertirAJson(datos));
        registro.setUsuario(usuario);

        bitacoraRepository.save(registro);
    }

    private String convertirAJson(Object objeto) {
        try {
            return new ObjectMapper().writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}