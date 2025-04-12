package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodegasRepository extends JpaRepository <BodegaBean, Long> {
    Optional<BodegaBean> findByUuid(String uuid);
}
