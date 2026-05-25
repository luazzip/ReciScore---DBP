package utec.reciscore.auth;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private utec.reciscore.auth.JwtService jwtService;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO();
        registerDTO.setEmail("luis@gmail.com");
        registerDTO.setPassword("123456");
        registerDTO.setName("Luis Nieva");
        registerDTO.setUsername("luisnieva");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("luis@gmail.com");
        loginDTO.setPassword("123456");

        authResponse = new AuthResponseDTO("token123");
    }

    @Test
    @WithMockUser
    void register_retorna201YToken() throws Exception {
        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    @WithMockUser
    void register_camposVacios_retorna400() throws Exception {
        RegisterRequestDTO vacio = new RegisterRequestDTO();

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_retorna200YToken() throws Exception {
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    @WithMockUser
    void login_credencialesInvalidas_retorna400() throws Exception {
        when(authService.login(any()))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }
}