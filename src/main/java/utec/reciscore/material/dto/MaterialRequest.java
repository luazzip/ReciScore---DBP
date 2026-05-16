package utec.reciscore.material.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Builder
public class MaterialRequest {
    private String name;
    private String category;
    private Boolean recyclable;
}
