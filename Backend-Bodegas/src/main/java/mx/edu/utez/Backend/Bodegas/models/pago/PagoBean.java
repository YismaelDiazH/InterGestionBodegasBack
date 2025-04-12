package mx.edu.utez.Backend.Bodegas.models.pago;

import jakarta.persistence.*;
import lombok.Data;
import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;

import java.time.LocalDate;
@Entity
@Data
@Table(name = "pagos")

public class PagoBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = true)
    private BodegaBean bodega;

    @Column(nullable = false)
    private Double monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BodegaBean getBodega() {
        return bodega;
    }

    public void setBodega(BodegaBean bodega) {
        this.bodega = bodega;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }
}
