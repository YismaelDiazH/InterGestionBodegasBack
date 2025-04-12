package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SedeRepository extends JpaRepository<SedeBean, Long> {
    Optional<SedeBean> findByUuid(String uuid);
}
