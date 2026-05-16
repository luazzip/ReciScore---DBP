package utec.reciscore.puntoMapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="punto_mapa")
@Data
public class PuntoMapa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="Latitud obligatoria")
    @DecimalMin(value="-90.0",message = "Latitud minima: -90")
    @DecimalMax(value = "90.0",  message = "Latitud máxima: 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud mínima: -180")
    @DecimalMax(value = "180.0",  message = "Longitud máxima: 180")
    @Column(nullable = false)
    private Double longitude;

}
