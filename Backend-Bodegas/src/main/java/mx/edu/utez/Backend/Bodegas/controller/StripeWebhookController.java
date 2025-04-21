package mx.edu.utez.Backend.Bodegas.controller;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import mx.edu.utez.Backend.Bodegas.services.PagosService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe-webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PagosService pagosService;

    public StripeWebhookController(PagosService pagosService) {
        this.pagosService = pagosService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Manejar diferentes tipos de eventos
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "payment_intent.amount_capturable_updated":
                    // Lógica para cuando el monto es capturable
                    break;
                default:
                    // Evento no manejado
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar webhook: " + e.getMessage());
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElseThrow();

        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;

            // Actualizar el pago en tu base de datos
            pagosService.actualizarEstadoPago(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    paymentIntent.getAmount() / 100.0
            );
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElseThrow();

        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;

            // Registrar el fallo del pago
            pagosService.actualizarEstadoPago(
                    paymentIntent.getId(),
                    "failed",
                    paymentIntent.getAmount() / 100.0
            );
        }
    }
}