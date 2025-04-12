package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import mx.edu.utez.Backend.Bodegas.repositories.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SedesService {
    @Autowired
    private SedeRepository sedeRepository;

    //regex
    private static final String NOMBRE_REGEX = "^(?!\\s*$)[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+(?: [a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+)*$"
            ;
    private static final String DIRECCION_REGEX = "^(?!\\s*$)[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ.,\\-]+(?: [a-zA-Z0-9áéíóúÁÉÍÓÚñÑ.,\\-]+)*$";
    private static final String ADMINISTRADOR_REGEX = "^(?!\\s*$)[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+(?: [a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+)*$";

    public List<SedeBean> obtenerTodasLasSedes() {
        return sedeRepository.findAll();
    }

    public Optional<SedeBean> buscarPorId(Long id) {
        return sedeRepository.findById(id);
    }

    public SedeBean crearSede(SedeBean sede) {
        validarSede(sede);
        sede.setUuid(UUID.randomUUID().toString());
        return sedeRepository.save(sede);
    }

    public Optional<SedeBean> actualizarSede(Long id, SedeBean nuevaSede) {
        return sedeRepository.findById(id)
                .map(sedeExistente -> {
                    sedeExistente.setNombre(nuevaSede.getNombre());
                    sedeExistente.setBodegas(nuevaSede.getBodegas());
                    return sedeRepository.save(sedeExistente);
                });
    }

    public void eliminarSede(Long id) {
        sedeRepository.deleteById(id);
    }

    public Optional<SedeBean> buscarPorUUID(String uuid) {
        return sedeRepository.findByUuid(uuid);
    }

    public void validarSede(SedeBean sede) {
        if (sede.getNombre() == null || sede.getNombre().isEmpty() || !sede.getNombre().matches(NOMBRE_REGEX)) {
            throw new IllegalArgumentException("El nombre de la sede es inválido.");
        }
        if (sede.getDireccion() == null || sede.getDireccion().isEmpty() || !sede.getDireccion().matches(DIRECCION_REGEX)) {
            throw new IllegalArgumentException("La dirección de la sede es inválida.");
        }
        if (sede.getAdministrador() == null || sede.getAdministrador().isEmpty() || !sede.getAdministrador().matches(ADMINISTRADOR_REGEX)) {
            throw new IllegalArgumentException("El administrador de la sede es inválido.");
        }
    }
}
