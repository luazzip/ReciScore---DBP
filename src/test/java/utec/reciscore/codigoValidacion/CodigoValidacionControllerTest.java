package utec.reciscore.codigoValidacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import utec.reciscore.codigoValidacion.application.CodigoValidacionController;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionRequestDTO;
import utec.reciscore.codigoValidacion.dto.CodigoValidacionResponseDTO;
import utec.reciscore.codigoValidacion.model.CodigoValidacionService;
import utec.reciscore.material.application.MaterialController;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = CodigoValidacionController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class CodigoValidacionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CodigoValidacionService codigoService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private CodigoValidacionResponseDTO responseDTO;
    private CodigoValidacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new CodigoValidacionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCodigo("A3F9B2C1");
        responseDTO.setUserId(1L);
        responseDTO.setPuntoMapaId(1L);
        responseDTO.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        responseDTO.setUsado(false);

        requestDTO = new CodigoValidacionRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setPuntoMapaId(1L);
    }

    @Test
    @WithMockUser
    void shouldReturn201WhenCodigoIsGenerated() throws Exception {
        when(codigoService.generarCodigo(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/codigos-validacion/generar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("A3F9B2C1"))
                .andExpect(jsonPath("$.usado").value(false));
    }

    @Test
    @WithMockUser
    void shouldReturn200WhenCodigoIsValid() throws Exception {
        when(codigoService.verificarCodigo("A3F9B2C1")).thenReturn(true);

        mockMvc.perform(post("/codigos-validacion/verificar/A3F9B2C1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenCodigoAlreadyUsed() throws Exception {
        when(codigoService.verificarCodigo("USADO123"))
                .thenThrow(new IllegalArgumentException("El código ya fue usado"));

        mockMvc.perform(post("/codigos-validacion/verificar/USADO123")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}