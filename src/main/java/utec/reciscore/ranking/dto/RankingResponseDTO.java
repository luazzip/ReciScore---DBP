package utec.reciscore.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingResponseDTO {
    private Long userId;
    private String username;
    private String name;
    private Integer points;
    private String location;
    private Integer nivel;
    private Integer posicion;
}