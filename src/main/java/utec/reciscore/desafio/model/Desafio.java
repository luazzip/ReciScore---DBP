package utec.reciscore.desafio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    //relacion con usuarios
    @ManyToMany
    @JoinTable(
            name = "user_desafio",
            joinColumns = @JoinColumn(name = "desafio_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usuariosInscritos=new HashSet<>();
}
