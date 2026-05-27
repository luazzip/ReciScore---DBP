package utec.reciscore.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;
    @InjectMocks private AuthService authService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private User user;

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

        user = new User();
        user.setEmail("luis@gmail.com");
        user.setPassword("hashedPassword");
        user.setName("Luis Nieva");
        user.setUsername("luisnieva");
    }

    @Test
    void shouldReturnTokenWhenRegisterIsSuccessful() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(anyString(), anyLong(), anyString())).thenReturn("token123");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh123");

        AuthResponseDTO response = authService.register(registerDTO);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnTokenWhenLoginCredentialsAreValid() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyLong(), anyString())).thenReturn("token123");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh123");

        AuthResponseDTO response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(loginDTO));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(loginDTO));
    }
}