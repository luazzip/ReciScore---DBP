package utec.reciscore.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import utec.reciscore.email.UsuarioRegistradoEvent;
import utec.reciscore.exceptions.ResourceNotFoundException;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    // ─────────────────────── REGISTER ───────────────────────

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());

        User saved = userRepository.save(user);

        // Evento asíncrono → EmailService envía bienvenida
        eventPublisher.publishEvent(
                new UsuarioRegistradoEvent(this, saved.getEmail(), saved.getName())
        );

        return buildResponse(saved);
    }

    // ─────────────────────── LOGIN ──────────────────────────

    public AuthResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return buildResponse(user);
    }

    // ─────────────────────── REFRESH ────────────────────────

    public AuthResponseDTO refresh(String refreshToken) {
        // Valida que sea un refresh token (no un access token reutilizado)
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Token inválido: no es un refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildResponse(user);
    }

    // ─────────────────────── HELPER ─────────────────────────

    private AuthResponseDTO buildResponse(User user) {
        String accessToken  = jwtService.generateToken(
                user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponseDTO(accessToken, refreshToken);
    }
}