package mx.edu.utez.Backend.Bodegas.controlller;

import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;
import mx.edu.utez.Backend.Bodegas.services.UsuariosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios/")
public class UsuariosController {
    private final UsuariosService usuarioService;

    public UsuariosController(UsuariosService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioBean> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<UsuarioBean> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioBean> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("rol/{rol}")
    public ResponseEntity<UsuarioBean> buscarPorRol(@PathVariable String rol) {
        Optional<UsuarioBean> usuario = usuarioService.BuscarRol(rol);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<UsuarioBean> buscarPorUUID(@PathVariable String uuid) {
        Optional<UsuarioBean> usuario = usuarioService.buscarPorUUID(uuid);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioBean> crearUsuario(@RequestBody UsuarioBean usuario) {
        UsuarioBean nuevoUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.status(201).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<UsuarioBean>> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioBean nuevoUsuario) {
        Optional<UsuarioBean> usuarioActualizado = usuarioService.actualizarUsuario(id, nuevoUsuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<UsuarioBean> usuario = usuarioService.buscarPorId(id);
        if (usuario.isPresent()) {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
