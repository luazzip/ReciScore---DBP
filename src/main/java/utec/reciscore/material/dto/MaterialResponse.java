package utec.reciscore.material.dto;

import lombok.*;
import utec.reciscore.material.model.TipoMaterial;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MaterialResponse {

    private Long id;
    private String name;
    private Double pointsPerKg;
    private Double weight;
    private TipoMaterial category;
    private Boolean recyclable;
}
