package utec.reciscore.puntoMapa.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.puntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.puntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.puntoMapa.model.PuntoMapaService;

import java.util.List;

@RestController
@RequestMapping("/puntos-mapa")
@RequiredArgsConstructor
public class PuntoMapaController {
    private final PuntoMapaService puntoMapaService;


    @PostMapping
    public ResponseEntity<PuntoMapaResponseDTO> crear(@Valid @RequestBody PuntoMapaRequestDTO dto) {
        return ResponseEntity.ok(puntoMapaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PuntoMapaResponseDTO>> listar() {
        return ResponseEntity.ok(puntoMapaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoMapaResponseDTO> buscarPorId(@PathVariable Long id) {
        PuntoMapaResponseDTO response=puntoMapaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        puntoMapaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarUbicacion(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(puntoMapaService.estaEnZonaValida(lat, lng));
    }
}