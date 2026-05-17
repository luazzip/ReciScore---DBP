package utec.reciscore.material.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class MaterialResponse {

    private Long id;
    private String name;
    private Double pointsPerKg;
    private String category;
    private Boolean recyclable;

}

