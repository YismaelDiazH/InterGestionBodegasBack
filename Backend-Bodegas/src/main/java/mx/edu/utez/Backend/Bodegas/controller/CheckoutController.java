package mx.edu.utez.Backend.Bodegas.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.Data;
import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.repositories.BodegasRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.InitializingBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController implements InitializingBean {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    private final BodegasRepository bodegaRepository;

    public CheckoutController(BodegasRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }
    @Override
    public void afterPropertiesSet() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/create-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
        try {
            Stripe.apiKey = stripeSecretKey;
            // Validar que la bodega existe
            BodegaBean bodega = bodegaRepository.findById(request.getBodegaId())
                    .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));

            // Crear parámetros para la sesión de Checkout
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("mxn")
                                                    .setUnitAmount((long) (bodega.getPrecio() * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Renta de Bodega " + bodega.getFolio())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("bodega_id", String.valueOf(bodega.getId()))
                    .putMetadata("cliente_id", request.getClienteId().toString());

            Session session = Session.create(paramsBuilder.build());

            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error al crear sesión de pago: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(@RequestParam String session_id) {
        try {
            Session session = Session.retrieve(session_id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", session.getId());
            response.put("payment_status", session.getPaymentStatus());
            response.put("customer_email", session.getCustomerEmail());
            response.put("amount_total", session.getAmountTotal());
            response.put("status", session.getStatus());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error al obtener información de la sesión");
        }
    }

    @Data
    public static class CheckoutRequest {
        private Long bodegaId;
        private Long clienteId;
    }
}