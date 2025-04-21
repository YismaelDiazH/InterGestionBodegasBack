package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<PagoBean, Long> {
    Optional<PagoBean> findByUuid(String uuid);

    List<PagoBean> findAllByRenta_Id(Long rentaId);

    Optional<PagoBean> findByPaymentIntentId(String paymentIntentId);
}
