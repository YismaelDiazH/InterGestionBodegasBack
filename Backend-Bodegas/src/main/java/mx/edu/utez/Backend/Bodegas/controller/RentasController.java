package mx.edu.utez.Backend.Bodegas.controller;

import com.stripe.model.PaymentIntent;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.services.RentasService;
import mx.edu.utez.Backend.Bodegas.services.StripeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/rentas/")
public class RentasController {
    private static final Logger logger = LogManager.getLogger(RentasController.class);

    private final StripeService stripeService;
    private RentasService rentasService;

    public RentasController(RentasService rentasService, StripeService stripeService) {
        this.rentasService = rentasService;
        this.stripeService = stripeService;
    }

    @GetMapping
    public List<RentaBean> obtenerTodasLasRentas() {
        logger.info("GET /api/rentas/ - Obteniendo listado completo de rentas");
        List<RentaBean> rentas = rentasService.obtenerTodasLasRentas();
        logger.debug("Total de rentas encontradas: {}", rentas.size());
        return rentas;
    }

    @GetMapping("id/{id}")
    public ResponseEntity<RentaBean> buscarPorId(@PathVariable Long id) {
        logger.info("GET /api/rentas/id/{} - Buscando renta por ID", id);
        Optional<RentaBean> renta = rentasService.buscarPorId(id);
        if (renta.isEmpty()) {
            logger.warn("Renta con ID {} no encontrada", id);
        } else {
            logger.debug("Renta encontrada: {}", renta.get());
        }
        return renta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("cliente/{clienteId}")
    public List<RentaBean> obtenerRentasPorCliente(@PathVariable Long clienteId) {
        logger.info("GET /api/rentas/cliente/{} - Buscando rentas por cliente", clienteId);
        List<RentaBean> rentas = rentasService.buscarPorCliente(clienteId);
        logger.debug("Encontradas {} rentas para el cliente {}", rentas.size(), clienteId);
        return rentas;
    }

    @GetMapping("bodega/{bodegaId}")
    public List<RentaBean> obtenerRentasPorBodega(@PathVariable Long bodegaId) {
        logger.info("GET /api/rentas/bodega/{} - Buscando rentas por bodega", bodegaId);
        List<RentaBean> rentas = rentasService.buscarPorBodega(bodegaId);
        logger.debug("Encontradas {} rentas para la bodega {}", rentas.size(), bodegaId);
        return rentas;
    }

    @PostMapping
    public ResponseEntity<RentaBean> crearRenta(@RequestBody RentaBean renta) {
        logger.info("POST /api/rentas/ - Creando nueva renta para bodega {} y cliente {}",
                renta.getBodega().getId(), renta.getCliente().getId());
        try {
            RentaBean nuevaRenta = rentasService.crearRenta(renta);
            logger.info("Renta creada exitosamente - ID: {}, UUID: {}",
                    nuevaRenta.getId(), nuevaRenta.getUuid());
            return ResponseEntity.status(201).body(nuevaRenta);
        } catch (Exception e) {
            logger.error("Error al crear renta: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/rentas/{id}/renovar")
    public ResponseEntity<?> renovarRenta(@PathVariable Long id) {
        try {
            // 1. Obtener la renta actual
            RentaBean rentaActual = rentasService.buscarPorId(id).orElseThrow();

            // 2. Crear Payment Intent para la renovación
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    (long) (rentaActual.getBodega().getPrecio() * 100),
                    "mxn",
                    "Renovación de bodega " + rentaActual.getBodega().getFolio()
            );

            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "paymentIntentId", paymentIntent.getId(),
                    "rentaId", id
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la renovación");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminarRenta(@PathVariable Long id) {
        logger.warn("DELETE /api/rentas/{} - Solicitada eliminación de renta", id);
        boolean eliminada = rentasService.eliminarRenta(id);
        if (eliminada) {
            logger.info("Renta {} eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.error("No se pudo eliminar: Renta con ID {} no encontrada", id);
            return ResponseEntity.notFound().build();
        }
    }
}