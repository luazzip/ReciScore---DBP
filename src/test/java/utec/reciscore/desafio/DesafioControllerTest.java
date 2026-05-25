package utec.reciscore.desafio;

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
import utec.reciscore.desafio.application.DesafioController;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.ListDesafioResponse;
import utec.reciscore.desafio.model.DesafioService;
import utec.reciscore.user.application.UserController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = DesafioController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class DesafioControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DesafioService desafioService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private DetailDesafioResponse detailResponse;
    private CreateDesafioRequest createRequest;

    @BeforeEach
    void setUp() {
        detailResponse = new DetailDesafioResponse();
        detailResponse.setId(1L);
        detailResponse.setTitulo("Recicla 3 botellas");
        detailResponse.setDescripcion("Recicla 3 botellas de plastico");

        createRequest = new CreateDesafioRequest();
        createRequest.setTitulo("Recicla 3 botellas");
        createRequest.setDescripcion("Recicla 3 botellas de plastico");
        createRequest.setCategoria("PLASTICO");
        createRequest.setFecha_inicio(LocalDateTime.now());
        createRequest.setFecha_fin(LocalDateTime.now().plusDays(7));
        createRequest.setPuntos(100);
        createRequest.setMeta_valor(3);
    }

    @Test
    @WithMockUser
    void createDesafio_retorna201() throws Exception {
        when(desafioService.createDesafio(any())).thenReturn(detailResponse);

        mockMvc.perform(post("/desafios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Recicla 3 botellas"));
    }

    @Test
    @WithMockUser
    void getAllDesafios_retorna200() throws Exception {
        ListDesafioResponse listResponse = new ListDesafioResponse();
        listResponse.setId(1L);
        listResponse.setTitulo("Recicla 3 botellas");
        when(desafioService.findAll()).thenReturn(List.of(listResponse));

        mockMvc.perform(get("/desafios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Recicla 3 botellas"));
    }

    @Test
    @WithMockUser
    void getDesafioById_retorna200() throws Exception {
        when(desafioService.findById(1L)).thenReturn(detailResponse);

        mockMvc.perform(get("/desafios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getDesafioById_noExiste_retorna404() throws Exception {
        when(desafioService.findById(99L))
                .thenThrow(new NoSuchElementException("No encontrado"));

        mockMvc.perform(get("/desafios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void unirse_retorna201() throws Exception {
        when(desafioService.unirse(1L, 1L)).thenReturn(detailResponse);

        mockMvc.perform(post("/desafios/1/unirse")
                        .with(csrf())
                        .param("userId", "1"))
                .andExpect(status().isCreated());
    }
}