package mx.edu.utez.Backend.Bodegas.models.pago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;

import java.time.LocalDate;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private double monto;

    private LocalDate fechaPago;

    @ManyToOne
    @JoinColumn(name = "renta_id")
    @JsonIgnoreProperties("pagos") // Evita recursividad
    private RentaBean renta;
}
