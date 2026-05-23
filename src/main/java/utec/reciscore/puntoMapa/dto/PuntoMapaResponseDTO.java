package utec.reciscore.puntoMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utec.reciscore.puntoMapa.model.TipoPunto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoMapaResponseDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String nombre;
    private TipoPunto tipo;
}
