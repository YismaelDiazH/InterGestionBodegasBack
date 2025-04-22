package mx.edu.utez.Backend.Bodegas.models.pago;

import lombok.Data;

@Data
public class ConfirmacionPagoDTO {
    private Long bodegaId;
    private Long clienteId;
    private String sessionId;
    private double monto;
    private String paymentStatus;
}
