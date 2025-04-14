package mx.edu.utez.Backend.Bodegas.models.sede;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;

import java.util.List;

@Entity
@Data
@Table(name = "sedes")
public class SedeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, length = 36)
    private String uuid;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @ManyToMany
    @JoinTable(
            name = "sede_administradores",
            joinColumns = @JoinColumn(name = "sede_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UsuarioBean> administradores;

    @OneToMany(mappedBy = "sede")
    @JsonIgnore
    private List<BodegaBean> bodegas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<BodegaBean> getBodegas() {
        return bodegas;
    }

    public void setBodegas(List<BodegaBean> bodegas) {
        this.bodegas = bodegas;
    }
}
