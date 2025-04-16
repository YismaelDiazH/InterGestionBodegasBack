package mx.edu.utez.Backend.Bodegas.models.bitacora;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
    @Table(name = "bitacora")
    public class BitacoraBean {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String accion; // Ej: "CREAR_BODEGA", "ACTUALIZAR_RENTA"

        @Column(nullable = false)
        private String entidadAfectada; // Ej: "Bodega", "Renta"

        @Column(nullable = false)
        private Long idEntidad; // ID del registro afectado

        @Column(nullable = false, columnDefinition = "TEXT")
        private String detalles; // JSON con datos relevantes

        @Column(nullable = false)
        private String usuario; // Email o username del usuario

        @Column(nullable = false, updatable = false)
        @CreationTimestamp
        private LocalDateTime fechaRegistro;

        // Getters y Setters

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Long idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(String entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
