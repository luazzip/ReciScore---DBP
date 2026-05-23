package utec.reciscore.material.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import utec.reciscore.material.model.TipoMaterial;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MaterialRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(message = "Los puntos por kg son obligatorios")
    @Positive(message = "Los puntos por kg deben ser mayores a 0")
    private Double pointsPerKg;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double weight;

    @NotNull(message = "La categoría es obligatoria")
    private TipoMaterial category;

    @NotNull(message = "El campo reciclable es obligatorio")
    private Boolean recyclable;
}