package mx.edu.utez.Backend.Bodegas.controlller;

import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import mx.edu.utez.Backend.Bodegas.services.SedesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/sedes/")
//@CrossOrigin("*")
public class SedesController {
    @Autowired
    private SedesService sedeService;

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
    public ResponseEntity<SedeBean> crearSede(@RequestBody SedeBean sede) {
        SedeBean nuevaSede = sedeService.crearSede(sede);
        return ResponseEntity.status(201).body(nuevaSede);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<SedeBean>> actualizarSede(@PathVariable Long id, @RequestBody SedeBean nuevaSede) {
        Optional<SedeBean> sedeActualizada = sedeService.actualizarSede(id, nuevaSede);
        return ResponseEntity.ok(sedeActualizada);
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
}
