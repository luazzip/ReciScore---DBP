package utec.reciscore.codigoValidacion.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodigoValidacionResponseDTO {
    private Long id;
    private String codigo;
    private Long userId;
    private Long puntoMapaId;
    private LocalDateTime fechaExpiracion;
    private Boolean usado;
}