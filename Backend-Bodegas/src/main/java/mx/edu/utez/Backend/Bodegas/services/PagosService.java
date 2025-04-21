package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.pago.PagoBean;
import mx.edu.utez.Backend.Bodegas.repositories.PagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagosService {

    private final PagoRepository pagosRepository;

    public PagosService(PagoRepository pagosRepository) {
        this.pagosRepository = pagosRepository;
    }

    // Métodos existentes...

    public PagoBean actualizarEstadoPago(String paymentIntentId, String status, Double amount) {
        Optional<PagoBean> pagoOpt = pagosRepository.findByPaymentIntentId(paymentIntentId);
        if (pagoOpt.isPresent()) {
            PagoBean pago = pagoOpt.get();
            pago.setPaymentStatus(status);
            if (amount != null) {
                pago.setMonto(amount);
            }
            return pagosRepository.save(pago);
        }
        return null;
    }

    public Optional<PagoBean> findByPaymentIntentId(String paymentIntentId) {
        return pagosRepository.findByPaymentIntentId(paymentIntentId);
    }

    public List<PagoBean> obtenerTodosLosPagos() {

        return List.of();
    }

    public Optional<PagoBean> buscarPorId(Long id) {
        return Optional.empty();
    }

    public Optional<PagoBean> buscarPorUUID(String uuid) {
        return Optional.empty();
    }

    public Optional<PagoBean> actualizarPago(Long id, PagoBean nuevoPago) {
        return Optional.empty();
    }

    public void eliminarPago(Long id) {
    }

    public List<PagoBean> obtenerPagosPorRenta(Long rentaId) {
        return List.of();
    }

    public PagoBean crearPago(PagoBean pago) {
        return pago;
    }
}