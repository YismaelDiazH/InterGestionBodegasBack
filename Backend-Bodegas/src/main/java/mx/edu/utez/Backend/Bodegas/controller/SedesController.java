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
import java.util.UUID;

@RestController
@RequestMapping("api/sedes/")

public class SedesController {
    @Autowired
    private SedesService sedeService;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<SedeBean> obtenerTodasLasSedes() {
        return sedeService.obtenerTodasLasSedes();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<SedeBean> buscarPorId(@PathVariable Long id) {
        Optional<SedeBean> sede = sedeService.buscarPorId(id);
        return sede.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<SedeBean> buscarPorUUID(@PathVariable String uuid) {
        Optional<SedeBean> sede = sedeService.buscarPorUUID(uuid);
        return sede.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearSede(@RequestBody SedeDto sedeDto) {
        try {
            // Crear una nueva entidad SedeBean
            SedeBean sede = new SedeBean();
            sede.setNombre(sedeDto.getNombre());
            sede.setDireccion(sedeDto.getDireccion());

            // Buscar los usuarios (administradores) por sus IDs
            List<UsuarioBean> administradores = usuarioRepository.findAllById(sedeDto.getAdministradores());

            // Verificar si todos los administradores existen
            if (administradores.size() != sedeDto.getAdministradores().size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Algunos administradores no existen en la base de datos.");
            }

            // Asignar los administradores a la sede
            sede.setAdministradores(administradores);

            // Guardar la sede en la base de datos
            sedeRepository.save(sede);

            return ResponseEntity.status(HttpStatus.CREATED).body(sede);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la sede.");
        }
    }

    public void validarSedeDTO(SedeDto sedeDTO) {
        if (sedeDTO.getNombre() == null || sedeDTO.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sede es obligatorio.");
        }
        if (sedeDTO.getDireccion() == null || sedeDTO.getDireccion().isEmpty()) {
            throw new IllegalArgumentException("La dirección de la sede es obligatoria.");
        }
        if (sedeDTO.getAdministradores() == null || sedeDTO.getAdministradores().isEmpty()) {
            throw new IllegalArgumentException("Debe asignarse al menos un administrador.");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<SedeBean> actualizarSede(@PathVariable Long id, @RequestBody SedeDto sedeDTO) {  // Añadimos @RequestBody
        return sedeRepository.findById(id)
                .map(sedeExistente -> {
                    sedeExistente.setNombre(sedeDTO.getNombre());
                    sedeExistente.setDireccion(sedeDTO.getDireccion());

                    // Actualizar administradores
                    if (sedeDTO.getAdministradores() != null && !sedeDTO.getAdministradores().isEmpty()) {
                        List<UsuarioBean> administradores = usuarioRepository.findAllById(sedeDTO.getAdministradores());
                        sedeExistente.setAdministradores(administradores);
                    }
                    SedeBean sedeActualizada = sedeRepository.save(sedeExistente);
                    return ResponseEntity.ok(sedeActualizada);  // Retornamos un 200 OK
                })
                .orElseGet(() -> ResponseEntity.notFound().build());  // Si no se encuentra la sede, retornamos 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Long id) {
        Optional<SedeBean> sede = sedeService.buscarPorId(id);
        if (sede.isPresent()) {
            sedeService.eliminarSede(id);
            return ResponseEntity.noContent().build();
        } else {
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
