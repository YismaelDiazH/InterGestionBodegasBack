package mx.edu.utez.Backend.Bodegas.controlller;

import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.services.PagosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//@CrossOrigin("*")
@RestController
@RequestMapping("api/pagos/")
public class PagosController {
    @Autowired
    private PagosService pagoService;

    @GetMapping
    public List<PagoBean> obtenerTodosLosPagos() {
        return pagoService.obtenerTodosLosPagos();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<PagoBean> buscarPorId(@PathVariable Long id) {
        Optional<PagoBean> pago = pagoService.buscarPorId(id);
        return pago.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<PagoBean> buscarPorUUID(@PathVariable String uuid) {
        Optional<PagoBean> pago = pagoService.buscarPorUUID(uuid);
        return pago.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PagoBean> crearPago(@RequestBody PagoBean pago) {
        PagoBean nuevoPago = pagoService.crearPago(pago);
        return ResponseEntity.status(201).body(nuevoPago);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<PagoBean>> actualizarPago(@PathVariable Long id, @RequestBody PagoBean nuevoPago) {
        Optional<PagoBean> pagoActualizado = pagoService.actualizarPago(id, nuevoPago);
        return ResponseEntity.ok(pagoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        Optional<PagoBean> pago = pagoService.buscarPorId(id);
        if (pago.isPresent()) {
            pagoService.eliminarPago(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
