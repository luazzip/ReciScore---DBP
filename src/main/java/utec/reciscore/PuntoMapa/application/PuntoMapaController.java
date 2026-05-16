package utec.reciscore.PuntoMapa.application;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.PuntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.PuntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.PuntoMapa.model.PuntoMapa;
import utec.reciscore.PuntoMapa.model.PuntoMapaService;

import java.util.List;

@RestController
@RequestMapping("/puntos-mapa")
public class PuntoMapaController {
    private final PuntoMapaService puntoMapaService;

    public PuntoMapaController(PuntoMapaService service) {
        this.puntoMapaService = service;
    }

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
        return puntoMapaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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