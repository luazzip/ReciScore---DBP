package utec.reciscore.reporteReciclaje.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utec.reciscore.material.model.Material;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_reciclaje")
@Data
@NoArgsConstructor
public class ReporteReciclaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroReporte;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "punto_mapa_id")
    private PuntoMapa puntoMapa;

    @NotBlank(message = "La foto es obligatoria")
    @Column(nullable = false)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tamaño es obligatorio")
    @Column(nullable = false)
    private TamanoObjeto tamanoObjeto;

    @NotNull(message = "El número de artículos es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 artículo")
    @Column(nullable = false)
    private Integer numeroArticulos;

    @Column(nullable = false)
    private Boolean materialDetectadoIa;

    @DecimalMin(value = "0.0", message = "Confianza mínima: 0%")
    @DecimalMax(value = "1.0", message = "Confianza máxima: 100%")
    private Double confianzaIa;

    @Column(nullable = false)
    private Boolean validadoIa;

    @Column(nullable = false)
    private Boolean gpsValidado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}