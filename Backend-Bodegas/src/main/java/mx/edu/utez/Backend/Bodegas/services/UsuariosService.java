package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.usuario.UsuarioBean;
import mx.edu.utez.Backend.Bodegas.repositories.UsuarioRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuariosService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LogManager.getLogger(UsuariosService.class);

    //REGEX patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?!\\s*$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^(?!\\s*$)\\d{10}$");
    private static final Pattern RFC_PATTERN = Pattern.compile("^(?!\\s*$)[A-ZÑ&]{3,4}\\d{6}[A-Z\\d]{3}$");
    private static final Pattern CODIGOPOS_PATTERN = Pattern.compile("^(?!\\s*$)\\d{5}$");
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^(?!\\s*$)(?=.{2,50}$)[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$");
    private static final Pattern APELLIDOPATERNO_PATTERN = Pattern.compile("^(?!\\s*$)(?=.{2,50}$)[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$");
    private static final Pattern APELLIDOMATERNO_PATTERN = Pattern.compile("^(?!\\s*$)(?=.{2,50}$)[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$");
    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^(?!\\s*$)(?=.{5,100}$)[A-ZÁÉÍÓÚÑa-záéíóúñ0-9#.,\\-]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ0-9#.,\\-]+)*$");
    private static final Pattern ROL_PATTERN = Pattern.compile("^(SUPERADMINISTRADOR|ADMINISTRADOR|CLIENTE)$", Pattern.CASE_INSENSITIVE);

    public List<UsuarioBean> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioBean> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioBean crearUsuario(UsuarioBean usuario) {
        validarUsuario(usuario);
        usuario.setUuid(UUID.randomUUID().toString());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        logger.info("Nuevo usuario creado con UUID: {}", usuario.getUuid());
        return usuarioRepository.save(usuario);
    }

    public Optional<UsuarioBean> actualizarUsuario(Long id, UsuarioBean nuevoUsuario) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setEmail(nuevoUsuario.getEmail());
                    if (nuevoUsuario.getPassword() != null && !nuevoUsuario.getPassword().isEmpty()) {
                        usuarioExistente.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
                        logger.info("Contraseña actualizada para usuario con ID: {}", id);
                    } else {
                        logger.info("Contraseña no modificada para usuario con ID: {}", id);
                    }
                    usuarioExistente.setRol(nuevoUsuario.getRol());
                    usuarioExistente.setNombre(nuevoUsuario.getNombre());
                    usuarioExistente.setApellidoMaterno(nuevoUsuario.getApellidoMaterno());
                    usuarioExistente.setApellidoPaterno(nuevoUsuario.getApellidoPaterno());
                    usuarioExistente.setTelefono(nuevoUsuario.getTelefono());
                    usuarioExistente.setRfc(nuevoUsuario.getRfc());
                    usuarioExistente.setDireccion(nuevoUsuario.getDireccion());
                    usuarioExistente.setCodigopos(nuevoUsuario.getCodigopos());
                    return usuarioRepository.save(usuarioExistente);
                });
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<UsuarioBean> buscarPorUUID(String uuid) {
        return usuarioRepository.findByUuid(uuid);
    }
    private void validarUsuario(UsuarioBean usuario){
        if(!EMAIL_PATTERN.matcher(usuario.getEmail()).matches()){
            logger.warn("Correo inválido: {}", usuario.getEmail());
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }
        if(!PASSWORD_PATTERN.matcher(usuario.getPassword()).matches()){
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula y un número");
        }
        if(!TELEFONO_PATTERN.matcher(usuario.getTelefono()).matches()){
            throw new IllegalArgumentException("El teléfono debe tener 10 dígitos");
        }
        if(!RFC_PATTERN.matcher(usuario.getRfc()).matches()){
            throw new IllegalArgumentException("El RFC no es válido");
        }
        if(!CODIGOPOS_PATTERN.matcher(usuario.getCodigopos()).matches()){
            throw new IllegalArgumentException("El código postal no es válido");
        }
        if(!NOMBRE_PATTERN.matcher(usuario.getNombre()).matches()){
            throw new IllegalArgumentException("El nombre no es válido");
        }
        if(!APELLIDOPATERNO_PATTERN.matcher(usuario.getApellidoPaterno()).matches()){
            throw new IllegalArgumentException("El apellido paterno no es válido");
        }
        if(!APELLIDOMATERNO_PATTERN.matcher(usuario.getApellidoMaterno()).matches()){
            throw new IllegalArgumentException("El apellido materno no es válido");
        }
        if(!DIRECCION_PATTERN.matcher(usuario.getDireccion()).matches()){
            throw new IllegalArgumentException("La dirección no es válida");
        }
        if (!ROL_PATTERN.matcher(usuario.getRol().name()).matches()) {
            throw new IllegalArgumentException("El rol no es válido");
        }
    }

    public Optional<UsuarioBean> BuscarRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }
}
