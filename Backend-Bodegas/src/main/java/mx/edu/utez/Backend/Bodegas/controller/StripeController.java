package mx.edu.utez.Backend.Bodegas.controller;

import lombok.Data;
import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.repositories.BodegasRepository;
import mx.edu.utez.Backend.Bodegas.repositories.UsuarioRepository;
import mx.edu.utez.Backend.Bodegas.services.PagosService;
import mx.edu.utez.Backend.Bodegas.services.RentasService;
import mx.edu.utez.Backend.Bodegas.services.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/pagos")
public class StripeController {

    private final StripeService stripeService;
    private final RentasService rentasService;
    private final PagosService pagosService;
    private final BodegasRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;

    public StripeController(StripeService stripeService, RentasService rentasService, PagosService pagosService, BodegasRepository bodegaRepository, UsuarioRepository usuarioRepository) {
        this.stripeService = stripeService;
        this.rentasService = rentasService;
        this.pagosService = pagosService;
        this.bodegaRepository = bodegaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/create-and-confirm-payment")
    public ResponseEntity<?> createAndConfirmPayment(@RequestBody PaymentRequest request) {
        try {
            // Validaciones básicas
            if (request.getBodegaId() == null || request.getClienteId() == null || request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Datos de pago incompletos o inválidos");
            }

            // 1. Verificar que la bodega existe y está disponible
            BodegaBean bodega = bodegaRepository.findById(request.getBodegaId())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

            if ("ocupada".equalsIgnoreCase(bodega.getStatus())) {
                return ResponseEntity.badRequest().body("La bodega ya está ocupada");
            }

            // 2. Verificar que el cliente existe
            usuarioRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            // 3. Crear renta y pago (igual que antes)
            RentaBean renta = new RentaBean();
            renta.setBodega(bodegaRepository.findById(request.getBodegaId()).orElseThrow());
            renta.setCliente(usuarioRepository.findById(request.getClienteId()).orElseThrow());
            renta.setFechaInicio(LocalDate.now());
            renta.setFechaFin(LocalDate.now().plusMonths(1));

            RentaBean rentaCreada = rentasService.crearRenta(renta);

            PagoBean pago = new PagoBean();
            pago.setMonto(request.getAmount());
            pago.setFechaPago(LocalDate.now());
            pago.setRenta(rentaCreada);

            pagosService.crearPago(pago);

            // 4. Actualizar estado de la bodega

            bodega.setStatus("ocupada");
            bodegaRepository.save(bodega);

            return ResponseEntity.ok(rentaCreada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar el pago: " + e.getMessage());
        }
    }
    // Clases DTO para las solicitudes
    @Data
    public static class PaymentRequest {
        private Long bodegaId;
        private double amount;
        private Long clienteId;
    }

    @Data
    public static class ConfirmPaymentRequest {
        private String paymentIntentId;
        private Long bodegaId;
        private Long clienteId;
        private double amount;
    }
}