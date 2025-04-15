package mx.edu.utez.Backend.Bodegas.controller;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.services.BodegasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/bodegas/")
public class BodegasController {
    private static final Logger logger = LogManager.getLogger(BodegasController.class);

    @Autowired
    private BodegasService bodegas_services;

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
        logger.info("Creando nueva bodega - Nombre: {}", bodega.getFolio());
        BodegaBean NuevaBodega = bodegas_services.CrearBodega(bodega);
        logger.info("Bodega creada - ID: {}, UUID: {}", NuevaBodega.getId(), NuevaBodega.getUuid());
        return ResponseEntity.status(201).body(NuevaBodega);
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