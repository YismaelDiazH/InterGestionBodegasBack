package mx.edu.utez.Backend.Bodegas.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.services.PagosService;
import mx.edu.utez.Backend.Bodegas.services.RentasService;
import mx.edu.utez.Backend.Bodegas.services.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripePaymentController {

    private final StripeService stripeService;
    private final RentasService rentasService;
    private final PagosService pagosService;

    public StripePaymentController(StripeService stripeService, RentasService rentasService, PagosService pagosService) {
        this.stripeService = stripeService;
        this.rentasService = rentasService;
        this.pagosService = pagosService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            Long rentaId = Long.parseLong(request.get("rentaId").toString());
            RentaBean renta = rentasService.buscarPorId(rentaId)
                    .orElseThrow(() -> new IllegalArgumentException("Renta no encontrada"));

            Long amount = (long) (renta.getBodega().getPrecio() * 100); // Convertir a centavos

            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    amount,
                    "mxn",
                    "Renta de bodega " + renta.getBodega().getFolio()
            );

            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "paymentIntentId", paymentIntent.getId(),
                    "amount", amount
            ));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error al crear el pago: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            Long rentaId = Long.parseLong(request.get("rentaId"));

            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                RentaBean renta = rentasService.buscarPorId(rentaId)
                        .orElseThrow(() -> new IllegalArgumentException("Renta no encontrada"));

                // Registrar el pago en la base de datos
                PagoBean pago = new PagoBean();
                pago.setMonto(paymentIntent.getAmount() / 100.0); // Convertir a pesos
                pago.setFechaPago(LocalDate.now());
                pago.setRenta(renta);
                pago.setPaymentIntentId(paymentIntentId);
                pago.setPaymentStatus(paymentIntent.getStatus());

                pagosService.crearPago(pago);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "paymentIntentId", paymentIntentId,
                        "pago", pago
                ));
            } else {
                return ResponseEntity.badRequest().body("El pago no fue exitoso");
            }
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error al confirmar el pago: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}