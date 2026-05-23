package utec.reciscore.codigoValidacion.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionRequestDTO;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionResponseDTO;
import utec.reciscore.codigoValidacion.infrastructure.CodigoValidacionRepository;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodigoValidacionService {

    private final CodigoValidacionRepository codigoRepository;
    private final UserRepository userRepository;
    private final PuntoMapaRepository puntoMapaRepository;

    public CodigoValidacionResponseDTO generarCodigo(CodigoValidacionRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        PuntoMapa puntoMapa = puntoMapaRepository.findById(dto.getPuntoMapaId())
                .orElseThrow(() -> new NoSuchElementException("Punto de mapa no encontrado"));

        CodigoValidacion codigo = new CodigoValidacion();
        codigo.setCodigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        codigo.setUsuario(user);
        codigo.setPuntoMapa(puntoMapa);

        CodigoValidacion saved = codigoRepository.save(codigo);
        return toDto(saved);
    }

    public boolean verificarCodigo(String codigo) {
        CodigoValidacion cv = codigoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException("Código no encontrado"));

        if (cv.getUsado()) {
            throw new IllegalArgumentException("El código ya fue usado");
        }

        if (cv.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El código ha expirado");
        }

        cv.setUsado(true);
        codigoRepository.save(cv);
        return true;
    }

    private CodigoValidacionResponseDTO toDto(CodigoValidacion cv) {
        CodigoValidacionResponseDTO dto = new CodigoValidacionResponseDTO();
        dto.setId(cv.getId());
        dto.setCodigo(cv.getCodigo());
        dto.setUserId(cv.getUsuario().getId());
        dto.setPuntoMapaId(cv.getPuntoMapa().getId());
        dto.setFechaExpiracion(cv.getFechaExpiracion());
        dto.setUsado(cv.getUsado());
        return dto;
    }
}