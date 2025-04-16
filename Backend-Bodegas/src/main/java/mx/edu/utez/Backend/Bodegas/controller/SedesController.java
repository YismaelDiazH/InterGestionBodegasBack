package mx.edu.utez.Backend.Bodegas.controller;

import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import mx.edu.utez.Backend.Bodegas.models.sede.SedeDto;
import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;
import mx.edu.utez.Backend.Bodegas.repositories.SedeRepository;
import mx.edu.utez.Backend.Bodegas.repositories.UsuarioRepository;
import mx.edu.utez.Backend.Bodegas.services.SedesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("api/sedes/")

public class SedesController {
    private static final Logger logger = LogManager.getLogger(SedesController.class);
    @Autowired
    private SedesService sedeService;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<SedeBean> obtenerTodasLasSedes() {
        logger.info("GET /api/sedes/ - Consultando todas las sedes");
        return sedeService.obtenerTodasLasSedes();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<SedeBean> buscarPorId(@PathVariable Long id) {
        logger.debug("GET /api/sedes/id/{} - Buscando sede por ID", id);
        Optional<SedeBean> sede = sedeService.buscarPorId(id);
        if (sede.isEmpty()) {
            logger.warn("Sede con ID {} no encontrada", id);
        }
        return sede.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<SedeBean> buscarPorUUID(@PathVariable String uuid) {
        Optional<SedeBean> sede = sedeService.buscarPorUUID(uuid);
        return sede.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearSede(@RequestBody SedeDto sedeDto) {
        logger.info("POST /api/sedes/ - Creando nueva sede: {}", sedeDto.getNombre());
        try {
            validarSedeDTO(sedeDto);

            SedeBean sede = new SedeBean();
            sede.setNombre(sedeDto.getNombre());
            sede.setDireccion(sedeDto.getDireccion());

            List<UsuarioBean> administradores = usuarioRepository.findAllById(sedeDto.getAdministradores());
            if (administradores.size() != sedeDto.getAdministradores().size()) {
                logger.error("Error al crear sede: Algunos administradores no existen - IDs solicitados: {}", sedeDto.getAdministradores());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Algunos administradores no existen");
            }

            sede.setAdministradores(administradores);
            SedeBean sedeCreada = sedeRepository.save(sede);

            logger.info("Sede creada exitosamente - ID: {}, Administradores: {}",
                    sedeCreada.getId(), sedeDto.getAdministradores());
            return ResponseEntity.status(HttpStatus.CREATED).body(sedeCreada);

        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al crear sede: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear sede: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error del servidor");
        }
    }
    private void validarSedeDTO(SedeDto sedeDTO) {
        if (sedeDTO.getNombre() == null || sedeDTO.getNombre().isEmpty()) {
            logger.error("Validación fallida: Nombre de sede vacío");
            throw new IllegalArgumentException("Nombre obligatorio");
        }
        if (sedeDTO.getDireccion() == null || sedeDTO.getDireccion().isEmpty()) {
            logger.error("Validación fallida: Dirección de sede vacía");
            throw new IllegalArgumentException("Dirección obligatoria");
        }
        if (sedeDTO.getAdministradores() == null || sedeDTO.getAdministradores().isEmpty()) {
            logger.error("Validación fallida: Sin administradores asignados");
            throw new IllegalArgumentException("Se requiere al menos 1 administrador");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SedeBean> actualizarSede(@PathVariable Long id, @RequestBody SedeDto sedeDTO) {
        logger.info("PUT /api/sedes/{} - Actualizando sede", id);
        return sedeRepository.findById(id)
                .map(sedeExistente -> {
                    sedeExistente.setNombre(sedeDTO.getNombre());
                    sedeExistente.setDireccion(sedeDTO.getDireccion());

                    if (sedeDTO.getAdministradores() != null) {
                        List<UsuarioBean> administradores = usuarioRepository.findAllById(sedeDTO.getAdministradores());
                        if (administradores.size() != sedeDTO.getAdministradores().size()) {
                            logger.warn("No se encontraron todos los administradores durante actualización - IDs: {}",
                                    sedeDTO.getAdministradores());
                        }
                        sedeExistente.setAdministradores(administradores);
                    }

                    SedeBean actualizada = sedeRepository.save(sedeExistente);
                    logger.info("Sede {} actualizada correctamente", id);
                    return ResponseEntity.ok(actualizada);
                })
                .orElseGet(() -> {
                    logger.warn("Intento de actualizar sede inexistente - ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Long id) {
        logger.warn("DELETE /api/sedes/{} - Solicitada eliminación de sede", id);
        if (sedeRepository.existsById(id)) {
            sedeService.eliminarSede(id);
            logger.info("Sede {} eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.error("No se pudo eliminar: Sede con ID {} no encontrada", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("usuario/correo/{correo}")
    public ResponseEntity<SedeBean> obtenerSedePorCorreo(@PathVariable String correo) {
        Optional<UsuarioBean> usuario = usuarioRepository.findByEmail(correo); // Busca al usuario por correo

        if (usuario.isPresent()) {
            Long usuarioId = (long) usuario.get().getId();  // Obtenemos el ID del usuario
            List<SedeBean> sedes = sedeRepository.findByAdministradores_Id(usuarioId); // Busca la sede(s) asociada(s) al usuario por su ID

            if (!sedes.isEmpty()) {
                return ResponseEntity.ok(sedes.get(0)); // Si se encuentra, retornamos la sede encontrada
            } else {
                return ResponseEntity.notFound().build(); // Si no tiene sede asociada, retornamos 404
            }
        } else {
            return ResponseEntity.notFound().build(); // Si no se encuentra el usuario, retornamos 404
        }
    }
}
