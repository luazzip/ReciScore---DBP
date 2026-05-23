package utec.reciscore.codigoValidacion.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CodigoValidacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El punto de mapa es obligatorio")
    private Long puntoMapaId;
}