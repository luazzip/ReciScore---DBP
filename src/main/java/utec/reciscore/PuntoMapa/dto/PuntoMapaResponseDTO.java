package utec.reciscore.PuntoMapa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoMapaResponseDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
}
