package utec.reciscore.ranking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utec.reciscore.ranking.dto.RankingResponseDTO;
import utec.reciscore.ranking.infrastructure.RankingRepository;
import utec.reciscore.ranking.model.RankingService;
import utec.reciscore.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @InjectMocks
    private RankingService rankingService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("luisnieva");
        user1.setName("Luis Nieva");
        user1.setPoints(200);
        user1.setLocation("Miraflores");
        user1.setNivel(3);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("ximena");
        user2.setName("Ximena Gamero");
        user2.setPoints(100);
        user2.setLocation("San Isidro");
        user2.setNivel(2);
    }

    @Test
    void shouldReturnRankingOrderedByPointsDesc() {
        when(rankingRepository.findAllOrderByPointsDesc()).thenReturn(List.of(user1, user2));

        List<RankingResponseDTO> ranking = rankingService.getRanking();

        assertNotNull(ranking);
        assertEquals(2, ranking.size());
        assertEquals(200, ranking.get(0).getPoints());
        assertEquals(100, ranking.get(1).getPoints());
    }

    @Test
    void shouldAssignCorrectPositionToEachUser() {
        when(rankingRepository.findAllOrderByPointsDesc()).thenReturn(List.of(user1, user2));

        List<RankingResponseDTO> ranking = rankingService.getRanking();

        assertEquals(1, ranking.get(0).getPosicion());
        assertEquals(2, ranking.get(1).getPosicion());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        when(rankingRepository.findAllOrderByPointsDesc()).thenReturn(List.of());

        List<RankingResponseDTO> ranking = rankingService.getRanking();

        assertNotNull(ranking);
        assertEquals(0, ranking.size());
    }

    @Test
    void shouldReturnFilteredRankingWhenLocationMatches() {
        when(rankingRepository.findByLocationOrderByPointsDesc("Miraflores"))
                .thenReturn(List.of(user1));

        List<RankingResponseDTO> ranking = rankingService.getRankingByLocation("Miraflores");

        assertNotNull(ranking);
        assertEquals(1, ranking.size());
        assertEquals("Miraflores", ranking.get(0).getLocation());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersInLocation() {
        when(rankingRepository.findByLocationOrderByPointsDesc("Barranco"))
                .thenReturn(List.of());

        List<RankingResponseDTO> ranking = rankingService.getRankingByLocation("Barranco");

        assertNotNull(ranking);
        assertEquals(0, ranking.size());
    }
}