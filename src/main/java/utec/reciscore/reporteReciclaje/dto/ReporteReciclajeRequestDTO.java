package utec.reciscore.reporteReciclaje.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utec.reciscore.reporteReciclaje.model.TamanoObjeto;

@Data
@NoArgsConstructor
public class ReporteReciclajeRequestDTO {

    @NotNull(message = "El material es obligatorio")
    private Long materialId;

    @NotBlank(message = "La foto es obligatoria")
    private String fotoUrl;

    @NotNull(message = "El tamaño es obligatorio")
    private TamanoObjeto tamanoObjeto;

    @NotNull(message = "El número de artículos es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 artículo")
    private Integer numeroArticulos;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
}