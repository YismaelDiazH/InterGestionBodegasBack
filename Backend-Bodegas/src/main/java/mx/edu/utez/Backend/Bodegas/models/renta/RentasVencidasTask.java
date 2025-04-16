package mx.edu.utez.Backend.Bodegas.models.renta;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.repositories.BodegasRepository;
import mx.edu.utez.Backend.Bodegas.repositories.RentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RentasVencidasTask {

    @Autowired
    private RentasRepository rentasRepository;

    @Autowired
    private BodegasRepository bodegasRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a media noche cada día
    public void liberarBodegasVencidas() {
        LocalDate hoy = LocalDate.now();
        List<RentaBean> rentasVencidas = rentasRepository.findByFechaFinAndRenovadaFalse(hoy);

        for (RentaBean renta : rentasVencidas) {
            BodegaBean bodega = renta.getBodega();
            bodega.setStatus("disponible");
            bodegasRepository.save(bodega);

            // Opcional: enviar notificación al cliente
        }
    }
}