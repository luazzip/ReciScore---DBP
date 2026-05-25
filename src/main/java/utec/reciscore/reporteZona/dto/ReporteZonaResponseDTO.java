package utec.reciscore.reporteZona.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReporteZonaResponseDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String descripcion;
    private String username;
    private LocalDateTime fecha;
    private boolean procesado;
}
