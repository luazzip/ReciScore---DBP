package utec.reciscore.reporteZona;

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
import utec.reciscore.exceptions.DuplicateReportException;
import utec.reciscore.reporteZona.application.ReporteZonaController;
import utec.reciscore.reporteZona.dto.ReporteZonaRequestDTO;
import utec.reciscore.reporteZona.dto.ReporteZonaResponseDTO;
import utec.reciscore.reporteZona.model.ReporteZonaService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ReporteZonaController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class ReporteZonaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ReporteZonaService reporteZonaService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ReporteZonaRequestDTO requestDTO;
    private ReporteZonaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ReporteZonaRequestDTO();
        requestDTO.setLatitude(-12.1191);
        requestDTO.setLongitude(-77.0308);
        requestDTO.setDescripcion("Zona sucia cerca del parque");

        responseDTO = new ReporteZonaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setLatitude(-12.1191);
        responseDTO.setLongitude(-77.0308);
        responseDTO.setDescripcion("Zona sucia cerca del parque");
        responseDTO.setUsername("reciuser");
        responseDTO.setFecha(LocalDateTime.now());
        responseDTO.setProcesado(false);
    }


    @Test
    @WithMockUser(roles = "USER")
    void create_requestValido_retorna200ConBody() throws Exception {
        when(reporteZonaService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/reportes-zona")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.latitude").value(-12.1191))
                .andExpect(jsonPath("$.longitude").value(-77.0308))
                .andExpect(jsonPath("$.username").value("reciuser"))
                .andExpect(jsonPath("$.procesado").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_descripcionOpcional_retorna200() throws Exception {
        requestDTO.setDescripcion(null);
        responseDTO.setDescripcion(null);
        when(reporteZonaService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/reportes-zona")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_zonaYaReportada_retorna409() throws Exception {
        when(reporteZonaService.create(any()))
                .thenThrow(new DuplicateReportException("Ya reportaste esta zona"));

        mockMvc.perform(post("/reportes-zona")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/reportes-zona")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    void getAll_retorna200ConLista() throws Exception {
        when(reporteZonaService.getAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/reportes-zona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("reciuser"))
                .andExpect(jsonPath("$[0].latitude").value(-12.1191))
                .andExpect(jsonPath("$[0].longitude").value(-77.0308));
    }

    @Test
    @WithMockUser
    void getAll_listaVacia_retorna200ConArrayVacio() throws Exception {
        when(reporteZonaService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/reportes-zona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getAll_variosReportes_retornaTodos() throws Exception {
        ReporteZonaResponseDTO response2 = new ReporteZonaResponseDTO();
        response2.setId(2L);
        response2.setLatitude(-11.0);
        response2.setLongitude(-76.0);
        response2.setUsername("reciuser2");
        response2.setFecha(LocalDateTime.now());
        response2.setProcesado(false);

        when(reporteZonaService.getAll()).thenReturn(List.of(responseDTO, response2));

        mockMvc.perform(get("/reportes-zona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("reciuser"))
                .andExpect(jsonPath("$[1].username").value("reciuser2"));
    }

    @Test
    void getAll_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/reportes-zona"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAll_reporteProcesado_apareceEnRespuesta() throws Exception {
        responseDTO.setProcesado(true);
        when(reporteZonaService.getAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/reportes-zona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].procesado").value(true));
    }
}