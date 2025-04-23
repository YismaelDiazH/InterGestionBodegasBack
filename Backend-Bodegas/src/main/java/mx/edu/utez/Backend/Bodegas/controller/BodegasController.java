package mx.edu.utez.Backend.Bodegas.controller;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.models.sede.SedeBean;
import mx.edu.utez.Backend.Bodegas.services.BodegasService;
import mx.edu.utez.Backend.Bodegas.services.SedesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("api/bodegas/")
public class BodegasController {
    private static final Logger logger = LogManager.getLogger(BodegasController.class);

    @Autowired
    private BodegasService bodegas_services;
    @Autowired
    private SedesService sedeService;

    @GetMapping
    public List<BodegaBean> ObtenerBodegas(){
        logger.info("Solicitando todas las bodegas");
        return bodegas_services.ObtenerTodas();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<BodegaBean> BuscarId(@PathVariable Long id){
        logger.info("Buscando bodega por ID: {}", id);
        Optional<BodegaBean> bodega = bodegas_services.BuscarID(id);
        if (bodega.isPresent()) {
            logger.debug("Bodega encontrada - ID: {}, Nombre: {}", id, bodega.get().getFolio());
        } else {
            logger.warn("Bodega no encontrada - ID: {}", id);
        }
        return ResponseEntity.ok(bodega.get());
    }

    @GetMapping("uuid/{id}")
    public ResponseEntity<BodegaBean> BuscarUiid(@PathVariable String uuid){
        logger.info("Buscando bodega por UUID: {}", uuid);
        Optional<BodegaBean> bodega = bodegas_services.BuscarPorUUID(uuid);
        if (bodega.isPresent()) {
            logger.debug("Bodega encontrada - UUID: {}, Nombre: {}", uuid, bodega.get().getFolio());
        } else {
            logger.warn("Bodega no encontrada - UUID: {}", uuid);
        }
        return ResponseEntity.ok(bodega.get());
    }

    @PostMapping
    public ResponseEntity<BodegaBean> crearBodega(@RequestBody BodegaBean bodega) {
        if (bodega.getSede() == null || bodega.getSede().getId() == 0) {
            logger.error("Intento de crear bodega sin sede válida");
            return ResponseEntity.badRequest().build();
        }

        // Obtener la sede completa desde la base de datos
        Optional<SedeBean> sedeOptional = sedeService.buscarPorId(bodega.getSede().getId());
        if (sedeOptional.isEmpty()) {
            logger.error("Sede no encontrada con ID: {}", bodega.getSede().getId());
            return ResponseEntity.notFound().build();
        }
        bodega.setSede(sedeOptional.get());

        // Generar el nombre automático
        String nombreGenerado = generarNombreBodega(bodega);
        bodega.setFolio(nombreGenerado);

        logger.info("Creando nueva bodega - Nombre generado: {}", nombreGenerado);

        BodegaBean nuevaBodega = bodegas_services.CrearBodega(bodega);

        logger.info("Bodega creada - ID: {}, Nombre: {}",
                nuevaBodega.getId(), nuevaBodega.getFolio());

        return ResponseEntity.status(201).body(nuevaBodega);
    }

    private String generarNombreBodega(BodegaBean bodega) {
        // Usar las dos primeras letras del nombre de la sede
        String inicialesSede = bodega.getSede().getNombre().substring(0, 2).toUpperCase();

        // Obtener la inicial del tamaño
        char inicialTamano;
        switch(bodega.getTamano().toUpperCase()) {
            case "CHICA": inicialTamano = 'C'; break;
            case "MEDIANA": inicialTamano = 'M'; break;
            case "GRANDE": inicialTamano = 'G'; break;
            default: inicialTamano = 'X';
        }

        // Generar dos caracteres aleatorios
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String randomChars = String.format("%c%c",
                caracteres.charAt(random.nextInt(caracteres.length())),
                caracteres.charAt(random.nextInt(caracteres.length())));

        return String.format("%s%c-%s", inicialesSede, inicialTamano, randomChars);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BodegaBean> actualizarBodega(@PathVariable Long id, @RequestBody BodegaBean nuevaBodega) {
        logger.info("Actualizando bodega - ID: {}", id);
        return bodegas_services.ActualizarBodega(id, nuevaBodega)
                .map(bodega -> {
                    logger.info("Bodega actualizada - ID: {}, Nuevo nombre: {}", id, bodega.getFolio());
                    return ResponseEntity.ok(bodega);
                })
                .orElseGet(() -> {
                    logger.warn("No se encontró bodega para actualizar - ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        logger.info("Eliminando bodega - ID: {}", id);
        Optional<BodegaBean> empresa = bodegas_services.BuscarID(id);
        if (empresa.isPresent()) {
            bodegas_services.EliminarBodega(id);
            logger.info("Bodega eliminada - ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("No se encontró bodega para eliminar - ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}