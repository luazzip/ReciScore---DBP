package utec.reciscore.desafio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDesafioRequest {
    @NotBlank(message = "El titulo no puede estar vacío")
    private String titulo;

    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descripcion;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;

    @NotNull(message = "El campo meta no puede ser nulo")
    @Min(value = 1, message = "La meta para cada desafio debe ser al menos 1")
    //max?
    private Integer meta_valor;

    @NotNull(message = "El campo puntos no puede ser nulo")
    @Min(value = 1, message = "Los puntos para cada desafio deben ser al menos 1")
    @Max(value = 1000, message = "Los puntos para cada desafio no pueden superar 1000")
    private Integer puntos;

    @NotNull
    private LocalDateTime fecha_inicio;

    @NotNull
    private LocalDateTime fecha_fin;
}
