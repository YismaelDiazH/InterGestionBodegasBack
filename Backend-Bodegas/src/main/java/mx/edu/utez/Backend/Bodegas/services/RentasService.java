package mx.edu.utez.Backend.Bodegas.services;

import aj.org.objectweb.asm.commons.Remapper;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.repositories.RentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentasService {

    @Autowired
    private RentasRepository rentasRepository;

    public List<RentaBean> obtenerTodasLasRentas() {
        return rentasRepository.findAll();
    }

    public Optional<RentaBean> buscarPorId(Long id) {
        return rentasRepository.findById(id);
    }

    public Optional<RentaBean> buscarPorUuid(String uuid) {
        return rentasRepository.findByUuid(uuid);
    }

    public List<RentaBean> buscarPorCliente(Long clienteId) {
        return rentasRepository.findByClienteId(clienteId);
    }

    public List<RentaBean> buscarPorBodega(Long bodegaId) {
        return rentasRepository.findByBodegaId(bodegaId);
    }

    public RentaBean crearRenta(RentaBean renta) {
        renta.setUuid(UUID.randomUUID().toString());
        renta.setFechaInicio(LocalDate.now());
        return rentasRepository.save(renta);
    }

    public Optional<RentaBean> actualizarRenta(Long id, RentaBean nuevaRenta) {
        return rentasRepository.findById(id)
                .map(rentaExistente -> {
                    rentaExistente.setFechaInicio(nuevaRenta.getFechaInicio());
                    rentaExistente.setFechaFin(nuevaRenta.getFechaFin());
                    rentaExistente.setCliente(nuevaRenta.getCliente());
                    rentaExistente.setBodega(nuevaRenta.getBodega());
                    return rentasRepository.save(rentaExistente);
                });
    }

    public boolean eliminarRenta(Long id) {
        if (rentasRepository.existsById(id)) {
            rentasRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<RentaBean> renovarRenta(Long id, RentaBean datosRenovacion) {
        return rentasRepository.findById(id)
                .map(rentaOriginal -> {
                    rentaOriginal.setRenovada(true);
                    rentasRepository.save(rentaOriginal);

                    RentaBean nuevaRenta = new RentaBean();
                    nuevaRenta.setCliente(rentaOriginal.getCliente());
                    nuevaRenta.setBodega(rentaOriginal.getBodega());
                    nuevaRenta.setFechaInicio(LocalDate.now());
                    nuevaRenta.setFechaFin(datosRenovacion.getFechaFin());
                    nuevaRenta.setRentaOriginal(rentaOriginal);
                    nuevaRenta.setUuid(UUID.randomUUID().toString());

                    return rentasRepository.save(nuevaRenta);
                });
    }


}