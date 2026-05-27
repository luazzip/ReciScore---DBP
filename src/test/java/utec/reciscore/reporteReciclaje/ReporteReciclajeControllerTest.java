package utec.reciscore.reporteReciclaje;

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
import utec.reciscore.puntoMapa.application.PuntoMapaController;
import utec.reciscore.reporteReciclaje.application.ReporteReciclajeController;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeRequestDTO;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeResponseDTO;
import utec.reciscore.reporteReciclaje.model.ReporteReciclajeService;
import utec.reciscore.reporteReciclaje.model.TamanoObjeto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ReporteReciclajeController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class ReporteReciclajeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ReporteReciclajeService reporteService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;


    private ReporteReciclajeResponseDTO responseDTO;
    private ReporteReciclajeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ReporteReciclajeResponseDTO();
        responseDTO.setNumeroReporte(1L);
        responseDTO.setUserId(1L);
        responseDTO.setUserName("Luis Nieva");
        responseDTO.setMaterialNombre("Botella PET");
        responseDTO.setGpsValidado(true);
        responseDTO.setValidadoIa(true);
        responseDTO.setFecha(LocalDateTime.now());

        requestDTO = new ReporteReciclajeRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setMaterialId(1L);
        requestDTO.setFotoUrl("http://foto.com/foto.jpg");
        requestDTO.setTamanoObjeto(TamanoObjeto.PEQUENO);
        requestDTO.setNumeroArticulos(3);
        requestDTO.setLatitud(-12.1191);
        requestDTO.setLongitud(-77.0308);
    }

    @Test
    @WithMockUser
    void shouldReturn200WhenReporteIsCreated() throws Exception {
        when(reporteService.crear(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/reportes-reciclaje")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroReporte").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithAllReportes() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/reportes-reciclaje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUserReporteHistory() throws Exception {
        when(reporteService.obtenerPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/reportes-reciclaje/historial/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("Luis Nieva"));
    }

    @Test
    @WithMockUser
    void shouldReturn200WhenReporteIdExists() throws Exception {
        when(reporteService.buscarPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/reportes-reciclaje/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroReporte").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenReporteIdDoesNotExist() throws Exception {
        when(reporteService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reportes-reciclaje/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldReturn204WhenReporteIsDeleted() throws Exception {
        mockMvc.perform(delete("/reportes-reciclaje/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}