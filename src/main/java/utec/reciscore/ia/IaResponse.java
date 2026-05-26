package utec.reciscore.ia;

import lombok.Data;

@Data
public class IaResponse {
    private String label;
    private String tipoMaterial;
    private Double confidence;
    private Boolean recyclable;
}