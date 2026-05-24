package utec.reciscore.ranking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utec.reciscore.ranking.dto.RankingResponseDTO;
import utec.reciscore.ranking.model.RankingService;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingResponseDTO>> getRanking() {
        return ResponseEntity.ok(rankingService.getRanking());
    }

    @GetMapping("/distrito/{location}")
    public ResponseEntity<List<RankingResponseDTO>> getRankingByLocation(@PathVariable String location) {
        return ResponseEntity.ok(rankingService.getRankingByLocation(location));
    }
}
