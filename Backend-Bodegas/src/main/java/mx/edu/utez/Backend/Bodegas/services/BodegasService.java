package mx.edu.utez.Backend.Bodegas.services;

import mx.edu.utez.Backend.Bodegas.models.bodega.BodegaBean;
import mx.edu.utez.Backend.Bodegas.repositories.BodegasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class BodegasService {
    @Autowired
    private BodegasRepository bodegas_Repository;

    //REGEX patterns
    private static final Pattern TIPO_PATTERN = Pattern.compile("^(?! )[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+){0,49}$");
    private static final Pattern FOLIO_PATTERN = Pattern.compile("^[A-Z0-9]{5,10}$");
    private static final Pattern PRECIO_PATTERN = Pattern.compile("^(?!\\s*$)\\d+(\\.\\d{1,2})?$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(DISPONIBLE|OCUPADO)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TAMANO_PATTERN = Pattern.compile("^(?! )[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+){0,49}$");
    private static final Pattern EDIFICIO_PATTERN = Pattern.compile("^(?! )[A-ZÁÉÍÓÚÑa-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑa-záéíóúñ]+){0,49}$");


    public List<BodegaBean> ObtenerTodas(){
        return bodegas_Repository.findAll();
    }

    public Optional<BodegaBean> BuscarID(Long id){
        return bodegas_Repository.findById(id);
    }

    public BodegaBean CrearBodega(BodegaBean bodega){
        validarBodega(bodega);
        bodega.setUuid(UUID.randomUUID().toString());
        return bodegas_Repository.save(bodega);
    }

    public Optional<BodegaBean> ActualizarBodega(Long id, BodegaBean nuevabodega) {
        return bodegas_Repository.findById(id)
                .map(bodegaExistente -> {
                    bodegaExistente.setTipo(nuevabodega.getTipo());
                    bodegaExistente.setPrecio(nuevabodega.getPrecio());
                    bodegaExistente.setStatus(nuevabodega.getStatus());
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
        if(!TIPO_PATTERN.matcher(bodega.getTipo()).matches()){
            throw new IllegalArgumentException("El tipo de bodega no es válido");
        }
        if(!FOLIO_PATTERN.matcher(bodega.getFolio()).matches()){
            throw new IllegalArgumentException("El folio de bodega no es válido");
        }
        if(!PRECIO_PATTERN.matcher(String.valueOf(bodega.getPrecio())).matches()){
            throw new IllegalArgumentException("El precio de bodega no es válido");
        }
        if(!STATUS_PATTERN.matcher(bodega.getStatus()).matches()){
            throw new IllegalArgumentException("El status de bodega no es válido");
        }
        if(!TAMANO_PATTERN.matcher(bodega.getTamano()).matches()){
            throw new IllegalArgumentException("El tamaño de bodega no es válido");
        }
        if(!EDIFICIO_PATTERN.matcher(bodega.getEdificio()).matches()){
            throw new IllegalArgumentException("El edificio de bodega no es válido");
        }
    }
}
