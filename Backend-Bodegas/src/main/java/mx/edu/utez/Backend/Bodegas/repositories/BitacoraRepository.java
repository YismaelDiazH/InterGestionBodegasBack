package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.bitacora.BitacoraBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BitacoraRepository extends JpaRepository<BitacoraBean, Long> {
    List<BitacoraBean> findByEntidadAfectadaOrderByFechaRegistroDesc(String entidad);
}
