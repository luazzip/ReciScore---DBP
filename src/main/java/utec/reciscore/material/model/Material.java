package utec.reciscore.material.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Material{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double pointsPerKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType category;

    private Boolean recyclable;

}
