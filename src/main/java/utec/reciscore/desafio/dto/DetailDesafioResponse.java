package utec.reciscore.desafio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailDesafioResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Integer meta_valor;
    private Integer puntos;
    private LocalDateTime fecha_inicio;
    private LocalDateTime fecha_fin;
    private Boolean activo;
}
