package mx.edu.utez.Backend.Bodegas.controller;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.services.BodegasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("api/bodegas/")
public class BodegasController {
    @Autowired
    private BodegasService bodegas_services;

    @GetMapping
    public List<BodegaBean> ObtenerBodegas(){
        return bodegas_services.ObtenerTodas();
    }

    @GetMapping("id/{id}")
    public ResponseEntity<BodegaBean> BuscarId(@PathVariable Long id){
        Optional<BodegaBean> bodega = bodegas_services.BuscarID(id);
        return ResponseEntity.ok(bodega.get());
    }

    @GetMapping("uuid/{id}")
    public ResponseEntity<BodegaBean> BuscarUiid(@PathVariable String uuid){
        Optional<BodegaBean> bodega = bodegas_services.BuscarPorUUID(uuid);
        return ResponseEntity.ok(bodega.get());
    }

    @PostMapping
    public ResponseEntity<BodegaBean> crearBodega(@RequestBody BodegaBean bodega) {
        BodegaBean NuevaBodega = bodegas_services.CrearBodega(bodega);
        return ResponseEntity.status(201).body(NuevaBodega);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodegaBean> actualizarBodega(@PathVariable Long id, @RequestBody BodegaBean nuevaBodega) {
        return bodegas_services.ActualizarBodega(id, nuevaBodega)
                .map(bodega -> ResponseEntity.ok(bodega))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        Optional<BodegaBean> empresa = bodegas_services.BuscarID(id);
        if (empresa.isPresent()) {
            bodegas_services.EliminarBodega(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
