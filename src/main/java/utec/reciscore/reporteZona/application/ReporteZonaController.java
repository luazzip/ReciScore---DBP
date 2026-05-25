package utec.reciscore.reporteZona.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.reporteZona.dto.ReporteZonaRequestDTO;
import utec.reciscore.reporteZona.dto.ReporteZonaResponseDTO;
import utec.reciscore.reporteZona.model.ReporteZonaService;

import java.util.List;

@RestController
@RequestMapping("/reportes-zona")
@RequiredArgsConstructor
public class ReporteZonaController {
    private final ReporteZonaService reporteZonaService;

    @PostMapping
    public ResponseEntity<ReporteZonaResponseDTO> create(
            @Valid @RequestBody ReporteZonaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)     // cambiar a 201
                .body(reporteZonaService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReporteZonaResponseDTO>> getAll() {

        return ResponseEntity.ok(
                reporteZonaService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reporteZonaService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
