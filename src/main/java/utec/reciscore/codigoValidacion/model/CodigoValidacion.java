package utec.reciscore.codigoValidacion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_validacion")
@Data
@NoArgsConstructor
public class CodigoValidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "punto_mapa_id", nullable = false)
    private PuntoMapa puntoMapa;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private Boolean usado = false;

    @PrePersist
    public void prePersist() {
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(10);
    }
}