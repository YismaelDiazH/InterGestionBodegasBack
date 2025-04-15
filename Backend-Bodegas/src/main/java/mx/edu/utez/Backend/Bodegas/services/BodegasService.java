package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.repositories.BodegasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class BodegasService {
    @Autowired
    private BodegasRepository bodegas_Repository;

    //REGEX patterns

    private static final Pattern PRECIO_PATTERN = Pattern.compile("^(?!\\s*$)\\d+(\\.\\d{1,2})?$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(VACANTE|OCUPADA|INDISPONIBLE)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TAMANO_PATTERN = Pattern.compile("^(?! )[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+){0,49}$");
    private static final Pattern DESCRIPCION_PATTERN = Pattern.compile("^(?! )[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+){0,99}$");


    public List<BodegaBean> ObtenerTodas(){
        return bodegas_Repository.findAll();
    }

    public Optional<BodegaBean> BuscarID(Long id){
        return bodegas_Repository.findById(id);
    }

    public BodegaBean CrearBodega(BodegaBean bodega) {
        generarFolioYUuid(bodega);
        validarBodega(bodega);

        return bodegas_Repository.save(bodega);
    }
    private void generarFolioYUuid(BodegaBean bodega) {
        String sedeNombre = bodega.getSede().getNombre();
        String sedePrefix = sedeNombre.length() >= 2 ? sedeNombre.substring(0, 2).toUpperCase() : sedeNombre.toUpperCase();

        long totalBodegas = bodegas_Repository.count() + 1;

        String inicialTamano = bodega.getTamano().substring(0, 1).toUpperCase();

        char letraAleatoria = (char) (new Random().nextInt(26) + 'A');
        int numeroAleatorio = new Random().nextInt(9) + 1;

        String folioGenerado = sedePrefix + totalBodegas + inicialTamano + "-" + letraAleatoria + numeroAleatorio;

        bodega.setFolio(folioGenerado);
        bodega.setUuid(UUID.randomUUID().toString());
    }

    public Optional<BodegaBean> ActualizarBodega(Long id, BodegaBean nuevabodega) {
        return bodegas_Repository.findById(id)
                .map(bodegaExistente -> {
                    bodegaExistente.setPrecio(nuevabodega.getPrecio());
                    bodegaExistente.setStatus(nuevabodega.getStatus());
                    bodegaExistente.setDescripcion(nuevabodega.getDescripcion());
                    return bodegas_Repository.save(bodegaExistente);
                });
    }

    public void EliminarBodega(Long id){
        bodegas_Repository.deleteById(id);
    }

    public Optional<BodegaBean> BuscarPorUUID(String uuid){
        return bodegas_Repository.findByUuid(uuid);
    }

    public void validarBodega(BodegaBean bodega)    {


        if(!PRECIO_PATTERN.matcher(String.valueOf(bodega.getPrecio())).matches()){
            throw new IllegalArgumentException("El precio de bodega no es válido");
        }
        if(!STATUS_PATTERN.matcher(bodega.getStatus()).matches()){
            throw new IllegalArgumentException("El status de bodega no es válido");
        }
        if(!TAMANO_PATTERN.matcher(bodega.getTamano()).matches()){
            throw new IllegalArgumentException("El tamaño de bodega no es válido");
        }
        if(!DESCRIPCION_PATTERN.matcher(bodega.getDescripcion()).matches()){
            throw new IllegalArgumentException("La descripción de bodega no es válido");
        }
    }
}
