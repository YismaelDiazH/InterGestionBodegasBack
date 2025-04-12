package mx.edu.utez.Backend.Bodegas.repositories;

import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioBean, Long> {
    Optional<UsuarioBean> findByUuid(String uuid);
    Optional<UsuarioBean> findByRol(String rol);
    Optional<UsuarioBean> findByEmail(String email);


}
