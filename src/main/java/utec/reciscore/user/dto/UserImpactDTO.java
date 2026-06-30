package utec.reciscore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserImpactDTO {
    private Integer totalReports;
    private Integer validatedReports;
    private Integer totalItems;
    private Double estimatedKg;
    private Double co2SavedKg;
    private Double waterSavedLiters;
    private Double treesEquivalent;
    private Integer plasticReports;
    private Integer paperReports;
    private Integer glassReports;
}