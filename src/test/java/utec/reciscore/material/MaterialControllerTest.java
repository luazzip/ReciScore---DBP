package utec.reciscore.material;

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
import utec.reciscore.material.application.MaterialController;
import utec.reciscore.material.dto.MaterialRequest;
import utec.reciscore.material.dto.MaterialResponse;
import utec.reciscore.material.model.MaterialService;
import utec.reciscore.material.model.TipoMaterial;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = MaterialController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class MaterialControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MaterialService materialService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;


    private MaterialResponse materialResponse;
    private MaterialRequest materialRequest;

    @BeforeEach
    void setUp() {
        materialResponse = new MaterialResponse(1L, "Botella PET", 10.0, 0.5,
                TipoMaterial.PLASTICO, true);

        materialRequest = new MaterialRequest();
        materialRequest.setName("Botella PET");
        materialRequest.setPointsPerKg(10.0);
        materialRequest.setWeight(0.5);
        materialRequest.setCategory(TipoMaterial.PLASTICO);
        materialRequest.setRecyclable(true);
    }

    @Test
    @WithMockUser
    void create_retorna201() throws Exception {
        when(materialService.create(any())).thenReturn(materialResponse);

        mockMvc.perform(post("/material")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Botella PET"));
    }

    @Test
    @WithMockUser
    void getAll_retorna200() throws Exception {
        when(materialService.getAll()).thenReturn(List.of(materialResponse));

        mockMvc.perform(get("/material"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Botella PET"));
    }

    @Test
    @WithMockUser
    void getById_retorna200() throws Exception {
        when(materialService.getById(1L)).thenReturn(materialResponse);

        mockMvc.perform(get("/material/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getById_noExiste_retorna404() throws Exception {
        when(materialService.getById(99L))
                .thenThrow(new NoSuchElementException("No encontrado"));

        mockMvc.perform(get("/material/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getByCategory_retorna200() throws Exception {
        when(materialService.getByCategory(TipoMaterial.PLASTICO))
                .thenReturn(List.of(materialResponse));

        mockMvc.perform(get("/material/category/PLASTICO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("PLASTICO"));
    }
}