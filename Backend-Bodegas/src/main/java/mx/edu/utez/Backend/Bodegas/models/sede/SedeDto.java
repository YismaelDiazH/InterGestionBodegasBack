package mx.edu.utez.Backend.Bodegas.models.sede;

import java.util.List;

public class SedeDto {
    private String nombre;
    private String direccion;
    private List<Long> administradores; // Lista de IDs de administradores

    // Getters y Setters
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

    public List<Long> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(List<Long> administradores) {
        this.administradores = administradores;
    }
}
