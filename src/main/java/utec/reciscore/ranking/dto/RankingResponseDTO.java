package utec.reciscore.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingResponseDTO {
    private Long userId;
    private String username;
    private String name;
    private Integer points;
    private String location;
    private Integer nivel;
    private Integer posicion;
}

