package utec.reciscore.desafio.model;

import jakarta.persistence.*;
import lombok.*;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_desafio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desafio_id", nullable = false)
    private Desafio desafio;

    @Column(nullable = false)
    private Integer progresoActual = 0;

    @Column(nullable = false)
    private Boolean completado = false;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime fechaInscripcion;

    @PrePersist
    public void prePersist() {
        this.fechaInscripcion = LocalDateTime.now();
    }
}
