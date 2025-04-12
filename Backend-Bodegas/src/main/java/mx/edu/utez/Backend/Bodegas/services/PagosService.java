package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class PagosService {
    @Autowired
    private PagoRepository pagoRepository;

    //REGEX patterns
    private static final String MONTO_PATTERN = "^(?!\\s*$)\\d+(\\.\\d{1,2})?$"
            ;
    private static final String FECHA_PATTERN = "^(?!\\s*$)\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
            ;

    public List<PagoBean> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<PagoBean> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public PagoBean crearPago(PagoBean pago) {
        validarPago(pago);
        pago.setUuid(UUID.randomUUID().toString());
        pago.setFechaPago(LocalDate.now());
        return pagoRepository.save(pago);
    }

    public Optional<PagoBean> actualizarPago(Long id, PagoBean nuevoPago) {
        return pagoRepository.findById(id)
                .map(pagoExistente -> {
                    pagoExistente.setMonto(nuevoPago.getMonto());
                    pagoExistente.setFechaPago(nuevoPago.getFechaPago());
                    pagoExistente.setBodega(nuevoPago.getBodega());
                    return pagoRepository.save(pagoExistente);
                });
    }

    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }

    public Optional<PagoBean> buscarPorUUID(String uuid) {
        return pagoRepository.findByUuid(uuid);
    }
    public void validarPago(PagoBean pago) {
        if (!pago.getMonto().toString().matches(MONTO_PATTERN)) {
            throw new IllegalArgumentException("El monto no es válido");
        }
        if (!pago.getFechaPago().toString().matches(FECHA_PATTERN)) {
            throw new IllegalArgumentException("La fecha no es válida");
        }
    }
}
