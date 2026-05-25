package utec.reciscore.reporteZona.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utec.reciscore.reporteZona.dto.ReporteZonaRequestDTO;
import utec.reciscore.reporteZona.dto.ReporteZonaResponseDTO;
import utec.reciscore.reporteZona.model.ReporteZonaService;

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




}
