package utec.reciscore.puntoMapa;

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
import utec.reciscore.material.application.MaterialController;
import utec.reciscore.puntoMapa.application.PuntoMapaController;
import utec.reciscore.puntoMapa.dto.PuntoMapaRequestDTO;
import utec.reciscore.puntoMapa.dto.PuntoMapaResponseDTO;
import utec.reciscore.puntoMapa.model.PuntoMapaService;
import utec.reciscore.puntoMapa.model.TipoPunto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PuntoMapaController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class PuntoMapaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PuntoMapaService puntoMapaService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;


    private PuntoMapaResponseDTO responseDTO;
    private PuntoMapaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new PuntoMapaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setLatitude(-12.1191);
        responseDTO.setLongitude(-77.0308);
        responseDTO.setNombre("Punto Kennedy");
        responseDTO.setTipo(TipoPunto.ACOPIO_OFICIAL);

        requestDTO = new PuntoMapaRequestDTO();
        requestDTO.setLatitude(-12.1191);
        requestDTO.setLongitude(-77.0308);
        requestDTO.setNombre("Punto Kennedy");
        requestDTO.setTipo(TipoPunto.ACOPIO_OFICIAL);
    }

    @Test
    @WithMockUser
    void crear_retorna200() throws Exception {
        when(puntoMapaService.crear(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/puntos-mapa")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Punto Kennedy"));
    }

    @Test
    @WithMockUser
    void listar_retorna200() throws Exception {
        when(puntoMapaService.obtenerTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/puntos-mapa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Punto Kennedy"));
    }

    @Test
    @WithMockUser
    void buscarPorId_retorna200() throws Exception {
        when(puntoMapaService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/puntos-mapa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void eliminar_retorna204() throws Exception {
        mockMvc.perform(delete("/puntos-mapa/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void validarUbicacion_retorna200() throws Exception {
        when(puntoMapaService.estaEnZonaValida(-12.1191, -77.0308)).thenReturn(true);

        mockMvc.perform(get("/puntos-mapa/validar")
                        .param("lat", "-12.1191")
                        .param("lng", "-77.0308"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}