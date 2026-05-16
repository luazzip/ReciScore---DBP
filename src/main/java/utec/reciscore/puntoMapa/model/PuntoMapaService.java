package utec.reciscore.puntoMapa.model;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import utec.reciscore.puntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.puntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PuntoMapaService {
    private static final double radio_permitido_metros = 50.0;

    @Autowired
    private final PuntoMapaRepository puntoMapaRepository;

    public PuntoMapaResponseDTO crear(PuntoMapaRequestDTO puntoMapa){
        PuntoMapa punto = new PuntoMapa();
        punto.setLatitude(puntoMapa.getLatitude());
        punto.setLongitude(puntoMapa.getLongitude());
        return toDto(puntoMapaRepository.save(punto));
    }

    public List<PuntoMapaResponseDTO> obtenerTodos() {
        return puntoMapaRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<PuntoMapaResponseDTO> buscarPorId(Long id) {
        return puntoMapaRepository.findById(id).map(this::toDto);
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
        return dto;
    }
}
