package mx.edu.utez.Backend.Bodegas.controller;

import com.stripe.model.Event;
import com.stripe.net.Webhook;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/stripe-webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final Logger logger = LogManager.getLogger(StripeWebhookController.class);

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            logger.info("Evento de Stripe recibido: {}", event.getType());

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    // Lógica para pagos exitosos
                    logger.info("Pago exitoso procesado");
                    break;
                case "payment_intent.payment_failed":
                    // Lógica para pagos fallidos
                    logger.warn("Pago fallido detectado");
                    break;
                default:
                    logger.info("Evento no manejado: {}", event.getType());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al procesar webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}