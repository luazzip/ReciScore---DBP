package utec.reciscore.PuntoMapa.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoMapaRequestDTO {

    @NotNull(message = "Latitud obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud mínima: -90")
    @DecimalMax(value = "90.0",  message = "Latitud máxima: 90")
    private Double latitude;

    @NotNull(message = "Longitud obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud mínima: -180")
    @DecimalMax(value = "180.0",  message = "Longitud máxima: 180")
    private Double longitude;
}
