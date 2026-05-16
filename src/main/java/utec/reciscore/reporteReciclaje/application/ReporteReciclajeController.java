package utec.reciscore.reporteReciclaje.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeRequestDTO;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeResponseDTO;
import utec.reciscore.reporteReciclaje.model.ReporteReciclajeService;

import java.util.List;

@RestController
@RequestMapping("/reportes-reciclaje")
@RequiredArgsConstructor
public class ReporteReciclajeController {
    private final ReporteReciclajeService reporteService;

    @PostMapping
    public ResponseEntity<ReporteReciclajeResponseDTO> crear(@Valid @RequestBody ReporteReciclajeRequestDTO dto) {
        return ResponseEntity.ok(reporteService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReporteReciclajeResponseDTO>> listarTodos() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @GetMapping("/historial/{userId}")
    public ResponseEntity<List<ReporteReciclajeResponseDTO>> listarPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(reporteService.obtenerPorUsuario(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteReciclajeResponseDTO> buscarPorId(@PathVariable Long id) {
        return reporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}