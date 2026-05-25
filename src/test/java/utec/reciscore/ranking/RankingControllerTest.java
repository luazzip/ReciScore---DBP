package utec.reciscore.ranking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import utec.reciscore.material.application.MaterialController;
import utec.reciscore.ranking.application.RankingController;
import utec.reciscore.ranking.dto.RankingResponseDTO;
import utec.reciscore.ranking.model.RankingService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = RankingController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class RankingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RankingService rankingService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private RankingResponseDTO rankingResponse;

    @BeforeEach
    void setUp() {
        rankingResponse = new RankingResponseDTO(
                1L, "luisnieva", "Luis Nieva", 200, "Miraflores", 3, 1);
    }

    @Test
    @WithMockUser
    void getRanking_retorna200() throws Exception {
        when(rankingService.getRanking()).thenReturn(List.of(rankingResponse));

        mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("luisnieva"))
                .andExpect(jsonPath("$[0].posicion").value(1));
    }

    @Test
    @WithMockUser
    void getRankingByLocation_retorna200() throws Exception {
        when(rankingService.getRankingByLocation("Miraflores"))
                .thenReturn(List.of(rankingResponse));

        mockMvc.perform(get("/ranking/distrito/Miraflores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Miraflores"));
    }

    @Test
    @WithMockUser
    void getRankingByLocation_sinUsuarios_retornaListaVacia() throws Exception {
        when(rankingService.getRankingByLocation("Barranco")).thenReturn(List.of());

        mockMvc.perform(get("/ranking/distrito/Barranco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}