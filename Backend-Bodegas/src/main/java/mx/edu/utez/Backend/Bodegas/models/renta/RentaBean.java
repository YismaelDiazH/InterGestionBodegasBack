package mx.edu.utez.Backend.Bodegas.models.renta;

import jakarta.persistence.*;
import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;

import java.time.LocalDate;

@Entity
@Table(name = "rentas")
public class RentaBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private UsuarioBean cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private BodegaBean bodega;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;
    @Column(unique = true, nullable = false)
    private String uuid;

    private boolean renovada;

    // Opcional: para asociar con la renta anterior (si fue renovación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renta_original_id")
    private RentaBean rentaOriginal;

    public RentaBean() {}

    // Getters y Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public UsuarioBean getCliente() { return cliente; }

    public void setCliente(UsuarioBean cliente) { this.cliente = cliente; }

    public BodegaBean getBodega() { return bodega; }

    public void setBodega(BodegaBean bodega) { this.bodega = bodega; }

    public LocalDate getFechaInicio() { return fechaInicio; }

    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }

    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public boolean isRenovada() { return renovada; }

    public void setRenovada(boolean renovada) { this.renovada = renovada; }

    public RentaBean getRentaOriginal() { return rentaOriginal; }

    public void setRentaOriginal(RentaBean rentaOriginal) { this.rentaOriginal = rentaOriginal; }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
