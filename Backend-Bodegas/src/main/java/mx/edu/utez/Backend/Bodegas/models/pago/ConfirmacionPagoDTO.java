package mx.edu.utez.Backend.Bodegas.models.pago;

import lombok.Data;

@Data
public class ConfirmacionPagoDTO {
    private Long rentaId;
    private String paymentIntentId;
    private String paymentStatus;
    private double monto;
}
