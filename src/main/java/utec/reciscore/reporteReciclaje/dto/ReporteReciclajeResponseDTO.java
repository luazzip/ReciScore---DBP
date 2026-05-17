package utec.reciscore.reporteReciclaje.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReporteReciclajeResponseDTO {
    private Long numeroReporte;
    private Long userId;
    private String userName;
    private String materialNombre;
    private String materialCategoria;
    private String fotoUrl;
    private String tamanoObjeto;
    private Integer numeroArticulos;
    private Boolean materialDetectadoIa;
    private Double confianzaIa;
    private Boolean validadoIa;
    private Boolean gpsValidado;
    private LocalDateTime fecha;
}
