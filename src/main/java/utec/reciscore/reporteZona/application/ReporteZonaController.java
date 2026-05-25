package utec.reciscore.reporteZona.application;

import lombok.RequiredArgsConstructor;
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
            @RequestBody ReporteZonaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                reporteZonaService.create(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<ReporteZonaResponseDTO>> getAll() {

        return ResponseEntity.ok(
                reporteZonaService.getAll()
        );
    }




}
