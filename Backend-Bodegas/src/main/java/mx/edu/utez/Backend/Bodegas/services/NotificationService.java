package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.controller.PagosController;
import mx.edu.utez.Backend.Bodegas.models.renta.RentaBean;
import mx.edu.utez.Backend.Bodegas.repositories.RentasRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(PagosController.class);
    private final RentasRepository rentasRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(RentasRepository rentasRepository, JavaMailSender mailSender) {
        this.rentasRepository = rentasRepository;
        this.mailSender = mailSender;
    }

    // Se ejecuta diariamente a las 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void notificarRentasPorVencer() {
        LocalDate unaSemanaDespues = LocalDate.now().plusDays(7);

        List<RentaBean> rentasPorVencer = rentasRepository
                .findByFechaFinAndRenovadaFalse(unaSemanaDespues);

        rentasPorVencer.forEach(this::enviarNotificacion);
    }

    private void enviarNotificacion(RentaBean renta) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(renta.getCliente().getEmail());
            mensaje.setSubject("Recordatorio: Tu renta está por vencer");
            mensaje.setText(crearMensaje(renta));

            mailSender.send(mensaje);
            logger.info("Notificación enviada al cliente: {}", renta.getCliente().getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar notificación: {}", e.getMessage());
        }
    }

    private String crearMensaje(RentaBean renta) {
        return String.format(
                "Hola %s,\n\n" +
                        "Tu renta de la bodega %s está por vencer el %s.\n" +
                        "Por favor, renueva tu renta o contacta al administrador.\n\n" +
                        "Saludos,\nEquipo de Bodegas",
                renta.getCliente().getNombre(),
                renta.getBodega().getFolio(),
                renta.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }
}