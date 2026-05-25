package utec.reciscore.user;

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
import utec.reciscore.user.application.UserController;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.dto.UserUpdateDTO;
import utec.reciscore.user.model.UserService;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService userService;
    @MockitoBean private utec.reciscore.auth.JwtService jwtService;

    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponseDTO();
        userResponse.setId(1L);
        userResponse.setEmail("luis@gmail.com");
        userResponse.setName("Luis Nieva");
        userResponse.setUsername("luisnieva");
        userResponse.setPoints(0);
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUserWhenIdExists() throws Exception {
        when(userService.getById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("luis@gmail.com"));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenUserIdDoesNotExist() throws Exception {
        when(userService.getById(99L)).thenThrow(new NoSuchElementException("No encontrado"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUpdatedDataWhenUpdateIsSuccessful() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setLocation("Miraflores");
        userResponse.setLocation("Miraflores");

        when(userService.update(eq(1L), any())).thenReturn(userResponse);

        mockMvc.perform(patch("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Miraflores"));
    }
}