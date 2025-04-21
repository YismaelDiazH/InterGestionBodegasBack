package mx.edu.utez.Backend.Bodegas.controller;

import mx.edu.utez.Backend.Bodegas.models.pago.ConfirmacionPagoDTO;
import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.repositories.PagoRepository;
import mx.edu.utez.Backend.Bodegas.repositories.RentasRepository;
import mx.edu.utez.Backend.Bodegas.services.PagosService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/pagos/")
public class PagosController {
    private static final Logger logger = LogManager.getLogger(PagosController.class);

    @Autowired
    private PagosService pagoService;
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private RentasRepository rentasRepository;

    @GetMapping
    public List<PagoBean> obtenerTodosLosPagos() {
        logger.info("GET /api/pagos/ - Solicitando listado completo de pagos");
        List<PagoBean> pagos = pagoService.obtenerTodosLosPagos();
        logger.debug("Se encontraron {} pagos en el sistema", pagos.size());
        return pagos;
    }
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarPago(@RequestBody ConfirmacionPagoDTO dto) {
        Optional<RentaBean> rentaOpt = rentasRepository.findById(dto.getRentaId());
        if (rentaOpt.isEmpty()) return ResponseEntity.badRequest().body("Renta no encontrada");

        PagoBean pago = new PagoBean();
        pago.setUuid(UUID.randomUUID().toString());
        pago.setMonto(dto.getMonto());
        pago.setFechaPago(LocalDate.now().now());
        pago.setPaymentIntentId(dto.getPaymentIntentId());
        pago.setPaymentStatus(dto.getPaymentStatus());
        pago.setRenta(rentaOpt.get());

        pagoRepository.save(pago);
        return ResponseEntity.ok("Pago registrado");
    }
    @GetMapping("id/{id}")
    public ResponseEntity<PagoBean> buscarPorId(@PathVariable Long id) {
        logger.info("GET /api/pagos/id/{} - Buscando pago por ID", id);
        Optional<PagoBean> pago = pagoService.buscarPorId(id);
        if (pago.isPresent()) {
            logger.debug("Pago encontrado: ID={}, Monto={}, Renta={}",
                    id, pago.get().getMonto(), pago.get().getRenta().getId());
        } else {
            logger.warn("Pago con ID {} no encontrado", id);
        }
        return pago.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<PagoBean> buscarPorUUID(@PathVariable String uuid) {
        logger.info("GET /api/pagos/uuid/{} - Buscando pago por UUID", uuid);
        Optional<PagoBean> pago = pagoService.buscarPorUUID(uuid);
        if (pago.isEmpty()) {
            logger.warn("Pago con UUID {} no encontrado", uuid);
        }
        return pago.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PagoBean> crearPago(@RequestBody PagoBean pago) {
        logger.info("POST /api/pagos/ - Creando nuevo pago para renta {}",
                pago.getRenta() != null ? pago.getRenta().getId() : "null");
        try {
            PagoBean nuevoPago = pagoService.crearPago(pago);
            logger.info("Pago creado exitosamente - ID: {}, Monto: {}, UUID: {}",
                    nuevoPago.getId(), nuevoPago.getMonto(), nuevoPago.getUuid());
            return ResponseEntity.status(201).body(nuevoPago);
        } catch (Exception e) {
            logger.error("Error al crear pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoBean> actualizarPago(@PathVariable Long id, @RequestBody PagoBean nuevoPago) {
        logger.info("PUT /api/pagos/{} - Actualizando pago", id);
        Optional<PagoBean> pagoActualizado = pagoService.actualizarPago(id, nuevoPago);
        if (pagoActualizado.isPresent()) {
            logger.info("Pago {} actualizado - Nuevo monto: {}", id, nuevoPago.getMonto());
            return ResponseEntity.ok(pagoActualizado.get());
        } else {
            logger.warn("No se encontró pago con ID {} para actualizar", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("renta/{rentaId}")
    public List<PagoBean> obtenerPagosPorRenta(@PathVariable Long rentaId) {
        logger.info("GET /api/pagos/renta/{} - Buscando pagos por renta", rentaId);
        List<PagoBean> pagos = pagoService.obtenerPagosPorRenta(rentaId);
        logger.debug("Encontrados {} pagos para la renta {}", pagos.size(), rentaId);
        return pagos;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        logger.warn("DELETE /api/pagos/{} - Solicitada eliminación de pago", id);
        Optional<PagoBean> pago = pagoService.buscarPorId(id);
        if (pago.isPresent()) {
            pagoService.eliminarPago(id);
            logger.info("Pago eliminado - ID: {}, Monto: {}", id, pago.get().getMonto());
            return ResponseEntity.noContent().build();
        } else {
            logger.error("No se pudo eliminar: Pago con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
    }
}