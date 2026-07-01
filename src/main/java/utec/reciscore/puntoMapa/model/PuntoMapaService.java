package utec.reciscore.puntoMapa.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import utec.reciscore.desafio.model.Desafio;
import utec.reciscore.puntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.puntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PuntoMapaService {
    private static final double radio_permitido_metros = 10000000000.0;

    private final PuntoMapaRepository puntoMapaRepository;
    private final ModelMapper modelMapper;

    public PuntoMapaResponseDTO crear(PuntoMapaRequestDTO puntoMapa){
        PuntoMapa punto = new PuntoMapa();
        punto.setLatitude(puntoMapa.getLatitude());
        punto.setLongitude(puntoMapa.getLongitude());
        punto.setNombre(puntoMapa.getNombre());
        punto.setTipo(puntoMapa.getTipo());
        return toDto(puntoMapaRepository.save(punto));
    }

    public List<PuntoMapaResponseDTO> obtenerTodos() {
        List<PuntoMapa> puntoMapas=puntoMapaRepository.findAll();

        if (puntoMapas.isEmpty()) {
            throw new NoSuchElementException("No existen puntos en el mapa registrados");
        }
        return puntoMapas.stream().map(this::toDto).toList();
    }

    public PuntoMapaResponseDTO buscarPorId(Long id) {
        PuntoMapa puntoMapa=puntoMapaRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("No se encontró el desafío con id: "+id));
        return modelMapper.map(puntoMapa,PuntoMapaResponseDTO.class);
    }

    public void eliminar(@PathVariable Long id) {
        puntoMapaRepository.deleteById(id);
    }

    public boolean estaEnZonaValida(double lat, double lng) {
        List<PuntoMapa> cercanos = puntoMapaRepository.findPuntosEnRadio(lat, lng, radio_permitido_metros);
        return !cercanos.isEmpty();
    }

    private PuntoMapaResponseDTO toDto(PuntoMapa punto) {
        PuntoMapaResponseDTO dto=new PuntoMapaResponseDTO();
        dto.setId(punto.getId());
        dto.setLatitude(punto.getLatitude());
        dto.setLongitude(punto.getLongitude());
        dto.setNombre(punto.getNombre());
        dto.setTipo(punto.getTipo());
        return dto;
    }
}
