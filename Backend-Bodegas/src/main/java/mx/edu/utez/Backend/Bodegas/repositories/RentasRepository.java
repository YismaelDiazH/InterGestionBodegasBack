package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentasRepository extends JpaRepository<RentaBean, Long> {
    Optional<RentaBean> findByUuid(String uuid);

    List<RentaBean> findByClienteId(Long clienteId);

    List<RentaBean> findByBodegaId(Long bodegaId);

    List<RentaBean> findByFechaFinAndRenovadaFalse(LocalDate unaSemanaDespues);
}