package utec.reciscore.desafio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "desafio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Desafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false,unique = true)
    private String categoria;

    @Min(value = 0, message = "La meta no puede ser negativa")
    @Column(nullable = false)
    private Integer meta_valor;

    @Min(value = 0, message = "Los puntos no pueden ser negativos")
    @Column(nullable = false)
    private Integer puntos;

    @Column(nullable = false)
    private LocalDateTime fecha_inicio;

    @Column(nullable = false)
    private LocalDateTime fecha_fin;

    @Column(nullable = false)
    private Boolean activo;
}
