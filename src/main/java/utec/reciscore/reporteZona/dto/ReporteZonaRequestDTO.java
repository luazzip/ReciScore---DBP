package utec.reciscore.reporteZona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReporteZonaRequestDTO {
    @NotNull(message = "La latitud no puede estar vacía")
    private Double latitude;

    @NotNull(message = "La longitud no puede estar vacío")
    private Double longitude;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String descripcion;
}
